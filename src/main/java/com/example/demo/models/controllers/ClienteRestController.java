package com.example.demo.models.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.models.entity.Cliente;
import com.example.demo.models.service.IClienteService;

@RestController
@RequestMapping("/api")
public class ClienteRestController {

	@Autowired
	private IClienteService clienteService;
	
	/****************GET ALL****************/
	
	@GetMapping("/clientes")
	public List<Cliente> index(){
		return clienteService.findAll();
	}
	
	/****************GET SHOW****************/
	
	@GetMapping("/clientes/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseEntity<?> show(@PathVariable Long id) {
		
		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();
		
		try{
		cliente = clienteService.findById(id);	
			
		}catch (DataAccessException e) {
			response.put("mensagem", "Erro ao realizar consulta no DB");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		if(cliente == null) {
			response.put("mensagem", "O Cliente: ".concat(id.toString().concat(" não existe!!!")));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
	}
	
	/****************POST****************/
	
	@PostMapping("/clientes")
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult result) {
		Cliente newCliente = null;
		Map<String, Object> response = new HashMap<>();
		
		if(result.hasErrors()) {
			
			List<String> errors = result.getFieldErrors().
						stream().
						map(err -> "O campo '"+err.getField() + "' "+err.getDefaultMessage()).
						collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST); 

		}
		
		try{
			newCliente = clienteService.save(cliente);
		}catch (DataAccessException e) {
			response.put("mensagem", "Erro ao inserir Cliente na base de dados");
			response.put("error",e.getMessage().concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR); 
		}
		
		response.put("mensagem", "Cliente inserido na base de dados com sucesso");
		response.put("cliente", newCliente);
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED); 
	}
	
	/****************PUT****************/
	
	@PutMapping("clientes/{id}")
	public ResponseEntity<?> update(@Valid @RequestBody Cliente cliente, BindingResult result, @PathVariable Long id) {
		
		Cliente clienteAtual = clienteService.findById(id);
		Cliente clienteUpdated = null;
		
		Map<String, Object> response = new HashMap<>();
		

		if(result.hasErrors()) {
			
			List<String> errors = result.getFieldErrors().
						stream().
						map(err -> "O campo '"+err.getField() + "' "+err.getDefaultMessage()).
						collect(Collectors.toList());
			response.put("errors", errors);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST); 

		}
		
		
		if(clienteAtual == null) {
			response.put("mensagem", "Erro: não foi possivel editar o ID: ".concat(id.toString()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
		}
		
		try {
		
		clienteAtual.setApelidio(cliente.getApelidio());
		clienteAtual.setNome(cliente.getNome());
		clienteAtual.setEmail(cliente.getEmail());
		
		clienteUpdated =  clienteService.save(clienteAtual);
		}catch (DataAccessException e) {
			response.put("mensagem", "Erro al atualizar o cliente na base");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		response.put("mensagem", "Atualizado na base com sucesso!!!");
		response.put("cliente", clienteUpdated);
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
		
		
		}
	
	/****************DELETE****************/
		
	@DeleteMapping("/clientes/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id) {
		Map<String, Object> response = new HashMap<>();

		try {
			clienteService.delete(id);
		}catch (DataAccessException e) {
			
			response.put("mensagem", "Erro ,  não foi possivel deletar o cliente na base");
			response.put("error", e.getMessage().concat(": ").concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);		
			
		}
			
		response.put("mensagem", "Cliente deletado da base");
        return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);		
	}}


































