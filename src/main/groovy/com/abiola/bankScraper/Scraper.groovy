package com.abiola.bankScraper

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.apache.log4j.Logger

import groovy.xml.MarkupBuilder;
import geb.Browser

Logger logger = Logger.getLogger(this.class);

def config = new ConfigSlurper().parse(new File("config.txt").toURI().toURL())

File lastSyncFile = new File( "lastSync.xml" );

SimpleDateFormat sf =  SimpleDateFormat.getInstance()
sf.applyPattern("yyyy.MM.dd HH:mm:ss Z")

def cal = Calendar.getInstance()
cal.add( Calendar.DAY_OF_MONTH, -1 )
Date lastSync = cal.getTime()

if( lastSyncFile.exists() ){
	def xml = new XmlSlurper().parse(lastSyncFile)
	lastSync = sf.parse(xml as String)
}

logger.info("Ultima Atualização em ${lastSync}")


def browser = new Browser()

def scraper = new ItauScraper(branch: config.branch.toString(), 
	account: config.account.toString() ,
	verifier:config.verifier.toString(), 
	password:config.password.toString(),
	saveDir:config.statmentFolder,
	lastSync: lastSync,
	browser: browser )


try{
	def statmentPath = scraper.scrape()
	logger.info("Salvando extrato em: $statmentPath")
	
	
	def exporter = new MyFinanceExporter( login: config.myFinanceLogin.toString(), password: config.myFinancePassoword.toString(), 
		accountName: config.myFinanceAccuntName.toString(), browser: browser )
	
	exporter.export(statmentPath);
	
	FileWriter fw = new FileWriter( lastSyncFile )
	def xml = new MarkupBuilder(fw)
	xml.lastSync( sf.format(Calendar.getInstance().getTime()) )
	fw.close();
}
catch( Exception e ){
	logger.error( e )
}
finally{
	browser.quit()
}
//XvfbService.getInstance().stop();

