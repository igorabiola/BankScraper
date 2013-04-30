package com.abiola.bankScraper.model

import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;

enum Type{
	DEBIT("Compra com cartao de debito acima de valor"),
	BALANCE("Saldo de Conta Corrente"),
	WITHDRAW("Cartão Itaú - Débito - Saque nacional aprovado")
	
	private final String subject;
	Type( String subject ){
		this.subject = subject;
	}
	
	public static Type getType( String subject ){
		return values().find{ t -> t.subject == subject } 
	}
}

class ItauNotification {
		
	public ItauNotification createNotification( MimeMessage message ){
		println("${message.subject} ${message.from}")
		processMessage( message )
	}
	
	def processMessage( MimeMessage message ){
		def text;
		Object content = message.getContent();
		if (content instanceof String)
		{
			text =  content
		}
		else if (content instanceof Multipart)
		{
			Multipart mp = (Multipart) content
			text = handleMultipart(mp)
		}
		
		def type = getType( message.subject )
		println type
		println ("**" + text + "**")
	}
	
	def getType( String subject ){
			
	}
	
	def handleMultipart( Multipart mp ){
		int count = mp.getCount();
		for (int i = 0; i < count; i++)
		{
			BodyPart bp = mp.getBodyPart(i);
			if ( bp.isMimeType("text/plain") ){
				Object content = bp.getContent();
				if (content instanceof String)
				{
					return content
				}
				else if (content instanceof InputStream)
				{
					// handle input stream
					return  content as String
				}
				else if (content instanceof Multipart)
				{
					Multipart mp2 = (Multipart)content;
					return handleMultipart(mp2);
				}
			}
		}
		return null;
	}

}
