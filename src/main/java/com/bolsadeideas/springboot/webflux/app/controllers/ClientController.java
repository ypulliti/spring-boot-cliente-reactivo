package com.bolsadeideas.springboot.webflux.app.controllers;

import com.bolsadeideas.springboot.webflux.app.models.dao.BankClientDao;
import com.bolsadeideas.springboot.webflux.app.models.documents.BankClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Controller
public class ClientController
{
    @Autowired
    private BankClientDao daoC;
    private static final Logger log = LoggerFactory.getLogger(ClientRestController.class);

    public Mono<BankClient> getPerson(final String id){

        return daoC.findById(id);
    }

    public void savePerson(BankClient person)
    {
        daoC.save(person).subscribe();
    }
    
    public void deletePerson(final String id)
    {
        daoC.deleteById(id).subscribe();
    }
}
