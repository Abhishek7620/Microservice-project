package com.codedecode.microservices.VaccinationCenter.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.codedecode.microservices.VaccinationCenter.Entity.VaccinationCenter;
import com.codedecode.microservices.VaccinationCenter.Model.Citizen;
import com.codedecode.microservices.VaccinationCenter.Model.RequiredResponse;
import com.codedecode.microservices.VaccinationCenter.Repos.CenterRepo;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;



@RestController
@RequestMapping("/vaccinationcenter")
public class VaccinationCenterController {
	
	@Autowired
	private CenterRepo centerRepo;
	
	@Autowired
	private RestTemplate restTemplate;
	
	public static final String VAC_CENTER ="vaccinationCenter" ;
	
	@PostMapping(path ="/add")
	public ResponseEntity<VaccinationCenter> addCitizen(@RequestBody VaccinationCenter vaccinationCenter) {
		
		VaccinationCenter vaccinationCenterAdded = centerRepo.save(vaccinationCenter);
		return new ResponseEntity<>(vaccinationCenterAdded, HttpStatus.OK);
	}
	
	@GetMapping(path = "/id/{id}")
	@CircuitBreaker(name=VAC_CENTER,fallbackMethod = "handleServiceTimeout")
	public ResponseEntity<RequiredResponse> getAllDadaBasedonCenterId(@PathVariable Integer id){
		RequiredResponse requiredResponse =  new RequiredResponse();
		VaccinationCenter center  = centerRepo.findById(id).get();
		requiredResponse.setCenter(center);
		java.util.List<Citizen> listOfCitizens = restTemplate.getForObject("http://CITIZEN-SERVICE/citizen/id/"+id, List.class);
		requiredResponse.setCitizens(listOfCitizens);
		return new ResponseEntity<RequiredResponse>(requiredResponse, HttpStatus.OK);
	}
	
	public ResponseEntity<RequiredResponse> handleServiceTimeout(@PathVariable Integer id, Exception e){
		RequiredResponse requiredResponse =  new RequiredResponse();
		VaccinationCenter center  = centerRepo.findById(id).get();
		requiredResponse.setCenter(center);
		return new ResponseEntity<RequiredResponse>(requiredResponse, HttpStatus.OK);
	}
	
	
	@GetMapping(path = "/get/{id}")
	public ResponseEntity<List<VaccinationCenter>> getVaccinationCenterById(@PathVariable Integer id){
		Optional<VaccinationCenter> center  = centerRepo.findById(id);
		List<VaccinationCenter> list = new ArrayList<VaccinationCenter>();
		if(center.isPresent()) {
			list.add(center.get());
		}
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	
	
	

}
