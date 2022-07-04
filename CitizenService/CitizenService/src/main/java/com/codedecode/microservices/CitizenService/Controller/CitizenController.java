package com.codedecode.microservices.CitizenService.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.codedecode.microservices.CitizenService.Entity.Citizen;
import com.codedecode.microservices.CitizenService.Model.RequiredResponseCitizen;
import com.codedecode.microservices.CitizenService.Model.VaccinationCenter;
import com.codedecode.microservices.CitizenService.repositories.CitizenRepo;
import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/citizen")
public class CitizenController {
	
	@Autowired
	private CitizenRepo repo; 
	
	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping(path ="/test")
	public ResponseEntity<String> test() {
		return new ResponseEntity<>("hello", HttpStatus.OK);
	}
	
	@RequestMapping(path ="/id/{id}")
	public ResponseEntity<java.util.List<Citizen>> getById(@PathVariable Integer id) {
		
		List<Citizen> listCitizen = repo.findByVaccinationCenterId(id);
		return new ResponseEntity<>(listCitizen, HttpStatus.OK);
	}
	
	@RequestMapping(path ="/get/{id}")
	public ResponseEntity<RequiredResponseCitizen> getByCitizenId(@PathVariable Integer id) {
		RequiredResponseCitizen requiredResponse =  new RequiredResponseCitizen();
		List<Citizen> listCitizen = new ArrayList<Citizen>();
		Optional<Citizen> citizen = repo.findById(id);
		if(citizen.isPresent()) {
			listCitizen.add(citizen.get());
			requiredResponse.setCitizen(citizen.get());
			ParameterizedTypeReference<List<VaccinationCenter>> typeRef = new ParameterizedTypeReference<List<VaccinationCenter>>() {
			};
			ResponseEntity<List<VaccinationCenter>> responseEntity = restTemplate.exchange("http://VACCINATION-CENTER-SERVICE/vaccinationcenter/get/"+citizen.get().getVaccinationCenterId(), HttpMethod.GET, null, typeRef);
			List<VaccinationCenter> vaccinationCenter = responseEntity.getBody();
			if(!vaccinationCenter.isEmpty()) {
				requiredResponse.setCenter(vaccinationCenter.get(0));
			}
		}
		
		return new ResponseEntity<RequiredResponseCitizen>(requiredResponse, HttpStatus.OK);
	}
	
	@PostMapping(path ="/add")
	public ResponseEntity<Citizen> addCitizen(@RequestBody Citizen newCitizen) {
		List<VaccinationCenter> vaccinationCenter = new ArrayList<VaccinationCenter>();
		vaccinationCenter = restTemplate.getForObject("http://VACCINATION-CENTER-SERVICE/vaccinationcenter/get/"+newCitizen.getVaccinationCenterId(), List.class);
		if(vaccinationCenter.isEmpty()) {
			return new ResponseEntity("Invalid Vaccination Center", HttpStatus.BAD_REQUEST);
		}else {
			Citizen citizen = repo.save(newCitizen);
			return new ResponseEntity<>(citizen, HttpStatus.OK);
		}
		
	}
	
	
	
}
