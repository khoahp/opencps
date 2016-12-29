package opencps.util;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

public class UnitTest {
	@Before
	public void setUp()
	throws Exception {
	 
	}
	 
	@After
	public void tearDown()
	throws Exception {
	 
	}
	 
	@Test
	public void test() {
	 
		assertTrue("OPENCPS",upperCase("opencps"));

	}
	
	
	protected String upperCase(String str) {
	    return StringUtil.upperCase(str);
	}
	 
	
}
