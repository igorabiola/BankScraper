import org.openqa.selenium.Dimension
import org.openqa.selenium.htmlunit.HtmlUnitDriver
import org.openqa.selenium.phantomjs.PhantomJSDriver
import org.openqa.selenium.phantomjs.PhantomJSDriverService
import org.openqa.selenium.remote.DesiredCapabilities

import com.gargoylesoftware.htmlunit.BrowserVersion

driver = { 

	//new FirefoxDriver()
//	return new HtmlUnitDriver(BrowserVersion.FIREFOX_17)
	/* Xvfb :1 -screen 0 1024x768x24 */
	//new ProcessBuilder("Xvfb", ':1', '-screen', '0', '1024x768x24').start();
	//XvfbService.getInstance().start()

//	FirefoxBinary firefoxBinary = new FirefoxBinary();
	//firefoxBinary.setEnvironmentProperty('DISPLAY', ':1')
//	return new FirefoxDriver(firefoxBinary, null);
	
	
		DesiredCapabilities caps = new DesiredCapabilities();
		caps.setCapability(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY, "/home/igor/apps/phantomjs-1.9.0-linux-x86_64/bin/phantomjs");
		
		

		caps.setCapability('phantomjs.page.settings.userAgent', 'Mozilla/5.0 (iPad; CPU OS 6_0 like Mac OS X) AppleWebKit/536.26 (KHTML, like Gecko) Version/6.0 Mobile/10A5355d Safari/8536.25');		
		caps.setCapability('phantomjs.page.settings.userAgent', 'Mozilla/5.0 (Windows NT 6.2; Win64; x64;) Gecko/20100101 Firefox/20.0');
		def driver = new PhantomJSDriver(caps)
		driver.manage().window().setSize(new Dimension(1028, 768))
		return driver

}

reportsDir = "reports/geb-reports"