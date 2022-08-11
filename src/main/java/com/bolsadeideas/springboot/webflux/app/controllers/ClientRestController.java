package com.bolsadeideas.springboot.webflux.app.controllers;

import com.bolsadeideas.springboot.webflux.app.models.dao.BankClientDao;
import com.bolsadeideas.springboot.webflux.app.models.documents.BankClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/clients")
public class ClientRestController
{
	@Autowired
	private BankClientDao daoC;

	private static final Logger log = LoggerFactory.getLogger(ClientRestController.class);
	
	@GetMapping
	public Flux<BankClient> showClients(){

		Flux<BankClient> productos = daoC.findAll()
				.map(producto -> {
					producto.setName(producto.getName().toUpperCase());
					return producto;
				})
				.doOnNext(prod -> log.info(prod.getName()));

		return productos;
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<BankClient>> ver(@PathVariable String id){
		return daoC.findById(id).map(p -> ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(p))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
	
	@PostMapping
	public Mono<ResponseEntity<Map<String, Object>>> crear(@RequestBody Mono<BankClient> bankClient){
		
		Map<String, Object> respuesta = new HashMap<String, Object>();
		
		return bankClient.flatMap(client -> {
			if(client.getCreateAt()==null) {
				client.setCreateAt(new Date());
			}
			
			return daoC.save(client).map(p-> {
				respuesta.put("BankClient", p);
				respuesta.put("mensaje", "Cliente creado con éxito");
				respuesta.put("timestamp", new Date());
				return ResponseEntity
					.created(URI.create("/api/clients/".concat(p.getId())))
					.contentType(MediaType.APPLICATION_JSON)
					.body(respuesta);
				});
			
		}).onErrorResume(t -> {
			return Mono.just(t).cast(WebExchangeBindException.class)
					.flatMap(e -> Mono.just(e.getFieldErrors()))
					.flatMapMany(Flux::fromIterable)
					.map(fieldError -> "El campo "+fieldError.getField() + " " + fieldError.getDefaultMessage())
					.collectList()
					.flatMap(list -> {
						respuesta.put("errors", list);
						respuesta.put("timestamp", new Date());
						respuesta.put("status", HttpStatus.BAD_REQUEST.value());
						return Mono.just(ResponseEntity.badRequest().body(respuesta));
					});
							
		});
		

	}
	
	@PutMapping("/{id}")
	public Mono<ResponseEntity<BankClient>> editar(@RequestBody BankClient bankClient, @PathVariable String id){
		return daoC.findById(id).flatMap(p -> {
			p.setName(bankClient.getName());
			p.setTypeClient(bankClient.getTypeClient());
			p.setBankAccounts(bankClient.getBankAccounts());
			return daoC.save(p);
		}).map(p->ResponseEntity.created(URI.create("/api/productos/".concat(p.getId())))
				.contentType(MediaType.APPLICATION_JSON)
				.body(p))
		.defaultIfEmpty(ResponseEntity.notFound().build());
	}


	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> eliminar(@PathVariable String id){
		return daoC.findById(id).flatMap(p ->{
			return daoC.delete(p).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}

}
