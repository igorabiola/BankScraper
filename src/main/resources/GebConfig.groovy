import org.openqa.selenium.firefox.FirefoxDriver

//def firefox = new FirefoxDriver()
//def chrome = new ChromeDriver()
//def htmlUnit = new HtmlUnitDriver()

// default is to use firefox
driver = { 
	//	DesiredCapabilities capabilities = DesiredCapabilities.chrome();
	//	capabilities.setCapability("chrome.binary", "/usr/bin/chromium-browser");
//	System.setProperty("webdriver.chrome.driver", "/home/igor/apps/chromedriver");
//	new ChromeDriver( ) 
	new FirefoxDriver()
}