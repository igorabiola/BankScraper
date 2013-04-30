package com.abiola.bankScraper

import javax.mail.Flags
import javax.mail.Folder;
import javax.mail.Session
import javax.mail.internet.MimeMessage
import javax.mail.search.FlagTerm

import org.springframework.context.ApplicationContext
import org.springframework.context.support.ClassPathXmlApplicationContext
import org.springframework.integration.Message
import org.springframework.integration.MessagingException
import org.springframework.integration.channel.DirectChannel
import org.springframework.integration.core.MessageHandler

import com.abiola.bankScraper.model.ItauNotification

def config = new ConfigSlurper().parse(new File("config.txt").toURI().toURL())

ApplicationContext ac = new ClassPathXmlApplicationContext("beans/integration-context.xml");


def clearFlags = {

	def props = ac.getBean("javaMailProperties")
	def session = Session.getDefaultInstance(props, null)
	def store = session.getStore("imaps")
	def inbox
	 
	
	  store.connect("imap.gmail.com", config.imapUserid.toString() , config.imapPassword.toString())
	  inbox =  store.getFolder(config.imapFolder.toString())
	  inbox.open(Folder.READ_WRITE)
	  
	  def messages = inbox.search(
		new FlagTerm(new Flags(Flags.Flag.DELETED), false))
	  messages.each { msg ->
		Flags siFlags = new Flags()
		siFlags.add("spring-integration-mail-adapter")
		msg.setFlags(siFlags, false)
		
	  }
	  inbox.close(true)
	  store.close()
}


DirectChannel inputChannel = ac.getBean("recieveChannel", DirectChannel.class);

inputChannel.subscribe(new MessageHandler () {
	public void handleMessage(Message<MimeMessage> message) throws MessagingException {
		def notfication =  new ItauNotification (message.getPayload() );
	}
});

clearFlags()
