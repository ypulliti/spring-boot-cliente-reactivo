package com.bolsadeideas.springboot.webflux.app.models.documents;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(collection="clients")
public class BankClient
{
	@Id
	private String id;
	private String name;
	private String typeClient;
	private Date createAt;

	private String[] bankAccounts;

	public BankClient() {}

	public BankClient(String nombre, String tipoClient, String[] bankAccount) {
		this.name = nombre;
		this.typeClient = tipoClient;
		this.bankAccounts = bankAccount;
	}

	public BankClient(String nombre, String tipoClient, String id, String bankAccount) {
		this.name = nombre;
		this.typeClient = tipoClient;
		this.id = id;
		this.bankAccounts = new String[] { bankAccount };
	}

}
