package com.bolsadeideas.springboot.webflux.app;

import java.util.Date;

import com.bolsadeideas.springboot.webflux.app.models.dao.BankClientDao;
import com.bolsadeideas.springboot.webflux.app.models.documents.BankClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner{

	@Autowired
	private BankClientDao dao;
	
	@Autowired
	private ReactiveMongoTemplate mongoTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);
	
	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception
	{
		mongoTemplate.dropCollection("clients").subscribe();
		
		Flux.just(new BankClient("Test01", "personal", "1", null),
				new BankClient("Test01", "empresarial", "2", null)
				)
		.flatMap(c -> {
			c.setCreateAt(new Date());
			return dao.save(c);
			})
		.subscribe(c -> log.info("Insert: " + c.getId() + " " + c.getName()));
		
	}
}
