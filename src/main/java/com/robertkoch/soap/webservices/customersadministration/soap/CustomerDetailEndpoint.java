package com.robertkoch.soap.webservices.customersadministration.soap;

import java.util.List;

import com.robertkoch.soap.webservices.customersadministration.bean.Customer;
import com.robertkoch.soap.webservices.customersadministration.service.CustomerDetailService;
import com.robertkoch.soap.webservices.customersadministration.soap.exception.CustomerNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import br.com.robertkoch.CustomerDetail;
import br.com.robertkoch.DeleteCustomerRequest;
import br.com.robertkoch.DeleteCustomerResponse;
import br.com.robertkoch.GetAllCustomerDetailRequest;
import br.com.robertkoch.GetAllCustomerDetailResponse;
import br.com.robertkoch.GetCustomerDetailRequest;
import br.com.robertkoch.GetCustomerDetailResponse;

@Endpoint
public class CustomerDetailEndpoint {

	@Autowired
	CustomerDetailService service;
	
	@PayloadRoot(namespace="http://robertkoch.com.br", localPart = "GetCustomerDetailRequest")
	@ResponsePayload
	public GetCustomerDetailResponse processCustomerDetailRequest(@RequestPayload GetCustomerDetailRequest req) throws Exception {

		Customer customer = service.findById(req.getId());
		if(customer == null) {
			throw new CustomerNotFoundException("Invalid Customer id "+req.getId());
		}
		return convertToGetCustomerDetailResponse(customer);
	}
	
	private GetCustomerDetailResponse convertToGetCustomerDetailResponse(Customer customer) {
		GetCustomerDetailResponse resp = new GetCustomerDetailResponse();
		resp.setCustomerDetail(convertToCustomerDetail(customer));
		return resp;
	}
	
	private CustomerDetail convertToCustomerDetail(Customer customer) {
		CustomerDetail customerDetail = new CustomerDetail();
		customerDetail.setId(customer.getId());
		customerDetail.setName(customer.getName());
		customerDetail.setPhone(customer.getPhone());
		customerDetail.setEmail(customer.getEmail());
		
		return customerDetail;
	}
	
	@PayloadRoot(namespace="http://robertkoch.com.br", localPart="GetAllCustomerDetailRequest")
	@ResponsePayload
	public GetAllCustomerDetailResponse processGetAllCustomerDetailRequest(@RequestPayload GetAllCustomerDetailRequest req) {
		List<Customer> customers = service.findAll();
		return convertToGetAllCustomerDetailResponse(customers);
	}
	
	private GetAllCustomerDetailResponse convertToGetAllCustomerDetailResponse(List<Customer> customers) {
		GetAllCustomerDetailResponse resp = new GetAllCustomerDetailResponse();
		customers.stream().forEach(c -> resp.getCustomerDetail().add(convertToCustomerDetail(c)));
		return resp;
	}
	 
	@PayloadRoot(namespace="http://robertkoch.com.br", localPart="DeleteCustomerRequest")
	@ResponsePayload
	public DeleteCustomerResponse deleteCustomerRequest(@RequestPayload DeleteCustomerRequest req) {
		DeleteCustomerResponse resp = new DeleteCustomerResponse();
		resp.setStatus(convertStatusSoap(service.deleteById(req.getId())));
		return resp;
	}
	
	private br.com.robertkoch.Status convertStatusSoap(com.robertkoch.soap.webservices.customersadministration.helper.Status statusService){
		if(statusService == com.robertkoch.soap.webservices.customersadministration.helper.Status.FAILURE) {
			return br.com.robertkoch.Status.FAILURE;
		}else {
			return br.com.robertkoch.Status.SUCCESS;
		}
	}
	
}
