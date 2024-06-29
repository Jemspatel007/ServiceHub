package com.dalhousie.servicehub.service;

import com.dalhousie.servicehub.dto.ServiceDto;
import com.dalhousie.servicehub.enums.ServiceType;
import com.dalhousie.servicehub.mapper.ServiceMapper;
import com.dalhousie.servicehub.model.ServiceModel;
import com.dalhousie.servicehub.repository.ServiceRepository;
import com.dalhousie.servicehub.response.GetServicesResponse;
import com.dalhousie.servicehub.service.dashboard_services.DashboardServicesImpl;
import com.dalhousie.servicehub.util.ResponseBody;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static com.dalhousie.servicehub.util.ResponseBody.ResultType.SUCCESS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DashBoardServicesTest {

    @Mock
    private ServiceRepository serviceRepository;

    @Mock
    private ServiceMapper serviceMapper;

    @InjectMocks
    private DashboardServicesImpl dashboardServices;

    @Test
    public void shouldGetAllServices_WhenServicesExist() {
        // Given
        List<ServiceModel> serviceModels = Arrays.asList(
                new ServiceModel(1L, "Service 1", "Description 1", 50.0, ServiceType.PLUMBING, 1L),
                new ServiceModel(2L, "Service 2", "Description 2", 60.0, ServiceType.ELECTRICIAN, 1L)
        );
        List<ServiceDto> serviceDtos = Arrays.asList(
                new ServiceDto(1L, "Description 1", "Service 1", 50.0, ServiceType.PLUMBING, 1L),
                new ServiceDto(2L, "Description 2", "Service 2", 60.0, ServiceType.ELECTRICIAN, 1L)
        );

        // When
        when(serviceRepository.findAll()).thenReturn(serviceModels);
        when(serviceMapper.toDto(any(ServiceModel.class))).thenAnswer(
                invocation -> {
                    ServiceModel model = invocation.getArgument(0);
                    return new ServiceDto(model.getId(), model.getDescription(), model.getName(), model.getPerHourRate(), model.getType(), model.getProviderId());
                }
        );

        ResponseBody<GetServicesResponse> responseBody = dashboardServices.getAllServices();

        // Then
        assertEquals(SUCCESS, responseBody.resultType());
        assertEquals(serviceDtos.size(), responseBody.data().getServices().size());
        verify(serviceRepository, times(1)).findAll();
        verify(serviceMapper, times(serviceModels.size())).toDto(any(ServiceModel.class));
    }

    @Test
    public void shouldGetServicesByType_WhenServicesExist_ForGivenType() {
        // Given
        ServiceType type = ServiceType.PLUMBING;
        List<ServiceModel> serviceModels = Arrays.asList(
                new ServiceModel(1L, "Service 1", "Description 1", 50.0, type, 1L),
                new ServiceModel(2L, "Service 2", "Description 2", 60.0, type, 1L)
        );
        List<ServiceDto> serviceDtos = Arrays.asList(
                new ServiceDto(1L, "Description 1", "Service 1", 50.0, type, 1L),
                new ServiceDto(2L, "Description 2", "Service 2", 60.0, type, 1L)
        );

        // When
        when(serviceRepository.findByType(type)).thenReturn(serviceModels);
        when(serviceMapper.toDto(any(ServiceModel.class))).thenAnswer(
                invocation -> {
                    ServiceModel model = invocation.getArgument(0);
                    return new ServiceDto(model.getId(), model.getDescription(), model.getName(), model.getPerHourRate(), model.getType(), model.getProviderId());
                }
        );

        ResponseBody<GetServicesResponse> responseBody = dashboardServices.getServicesByType(type);

        // Then
        assertEquals(SUCCESS, responseBody.resultType());
        assertEquals(serviceDtos.size(), responseBody.data().getServices().size());
        verify(serviceRepository, times(1)).findByType(type);
        verify(serviceMapper, times(serviceModels.size())).toDto(any(ServiceModel.class));
    }
    @Test
    public void shouldSearchServicesByName_WhenServicesExist_ForGivenName() {
        // Given
        String name = "Service";
        List<ServiceModel> serviceModels = Arrays.asList(
                new ServiceModel(1L, "Service 1", "Description 1", 50.0, ServiceType.PLUMBING, 1L),
                new ServiceModel(2L, "Service 2", "Description 2", 60.0, ServiceType.ELECTRICIAN, 1L)
        );
        List<ServiceDto> serviceDtos = Arrays.asList(
                new ServiceDto(1L, "Description 1", "Service 1", 50.0, ServiceType.PLUMBING, 1L),
                new ServiceDto(2L, "Description 2", "Service 2", 60.0, ServiceType.ELECTRICIAN, 1L)
        );

        // When
        when(serviceRepository.findByNameContainingIgnoreCase(name)).thenReturn(serviceModels);
        when(serviceMapper.toDto(any(ServiceModel.class))).thenAnswer(
                invocation -> {
                    ServiceModel model = invocation.getArgument(0);
                    return new ServiceDto(model.getId(), model.getDescription(), model.getName(), model.getPerHourRate(), model.getType(), model.getProviderId());
                }
        );

        ResponseBody<GetServicesResponse> responseBody = dashboardServices.searchServicesByName(name);

        // Then
        assertEquals(SUCCESS, responseBody.resultType());
        assertEquals(serviceDtos.size(), responseBody.data().getServices().size());
        verify(serviceRepository, times(1)).findByNameContainingIgnoreCase(name);
        verify(serviceMapper, times(serviceModels.size())).toDto(any(ServiceModel.class));
    }
}
