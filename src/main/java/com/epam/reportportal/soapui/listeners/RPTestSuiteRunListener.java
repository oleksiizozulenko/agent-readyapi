/*
 * Copyright 2016 EPAM Systems
 *
 *
 * This file is part of EPAM Report Portal.
 * https://github.com/reportportal/agent-java-soapui
 *
 * Report Portal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Report Portal is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Report Portal.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.epam.reportportal.soapui.listeners;

import com.epam.reportportal.soapui.service.SoapUIService;
import com.eviware.soapui.model.testsuite.TestCase;
import com.eviware.soapui.model.testsuite.TestCaseRunner;
import com.eviware.soapui.model.testsuite.TestSuiteRunContext;
import com.eviware.soapui.model.testsuite.TestSuiteRunListener;
import com.eviware.soapui.model.testsuite.TestSuiteRunner;
import com.eviware.soapui.plugins.ListenerConfiguration;

import static com.epam.reportportal.soapui.listeners.RPProjectRunListener.RP_SERVICE;

/**
 * Report portal related implementation of {@link TestSuiteRunListener}. This
 * listener should be used only with {@link RPTestRunListener} and
 * {@link RPProjectRunListener}.
 *
 * @author Raman_Usik
 */
@ListenerConfiguration
public class RPTestSuiteRunListener implements TestSuiteRunListener {

    private static final String SUITE_ONLY = "suite_only";
    private SoapUIService service;

    @Override
    public void afterRun(TestSuiteRunner runner, TestSuiteRunContext context) {
        final Object suiteOnly = context.getProperty(SUITE_ONLY);
        if (null != suiteOnly && ((Boolean) suiteOnly)) {
            service.finishTestSuite(runner);
            service.finishLaunch();
        }
    }

    @Override
    public void beforeRun(TestSuiteRunner runner, TestSuiteRunContext context) {
        service = (SoapUIService) context.getProperty(RP_SERVICE);
        if (null == service) {
            service = new SoapUIService(context.getTestSuite().getProject());
            service.startLaunch();
            service.startTestSuite(context.getTestSuite());

            context.setProperty(SUITE_ONLY, true);
            context.setProperty(RP_SERVICE, service);
        }
    }

    @Override
    public void beforeTestCase(TestSuiteRunner paramTestSuiteRunner, TestSuiteRunContext paramTestSuiteRunContext,
            TestCase paramTestCase) {
        service.startTestCase(paramTestCase);
    }

    @Override
    public void afterTestCase(TestSuiteRunner paramTestSuiteRunner, TestSuiteRunContext paramTestSuiteRunContext,
            TestCaseRunner paramTestCaseRunner) {
        service.finishTestCase(paramTestCaseRunner);
    }

}
