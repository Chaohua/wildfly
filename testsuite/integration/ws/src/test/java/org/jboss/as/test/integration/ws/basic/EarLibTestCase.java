/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.as.test.integration.ws.basic;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.jboss.as.test.integration.common.HttpRequest;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author <a href="klape@redhat.com">Kyle Lape</a>
 * @version $Revision: 1.1 $
 */
@RunWith(Arquillian.class)
@RunAsClient
public class EarLibTestCase {

    @ArquillianResource
    private URL url;

    @Deployment(testable = false)
    public static Archive<?> getDeployment() {
        final EnterpriseArchive ear = ShrinkWrap.create(EnterpriseArchive.class, "ws-app.ear");
        final WebArchive war = ShrinkWrap.create(WebArchive.class, "ws-example.war");
        final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "endpoint.jar");
        jar.addClasses(EndpointIface.class, PojoEndpoint.class, HelloObject.class);
        war.setWebXML(EarLibTestCase.class.getPackage(), "web.xml");
        ear.addAsDirectory("/lib");
        ear.add(jar, "/lib", ZipExporter.class);
        ear.add(war, "/", ZipExporter.class);
        return ear;
    }

    @Test
    public void testWSDL() throws Exception {
        String s = performCall("?wsdl");
        Assert.assertNotNull(s);
        //System.out.println(s);
        Assert.assertTrue(s.contains("wsdl:definitions"));
    }

    private String performCall(String params) throws Exception {
        URL url = new URL(this.url.toExternalForm() + "ws-example/" + params);
        //System.out.println(url.toExternalForm());
        return HttpRequest.get(url.toExternalForm(), 30, TimeUnit.SECONDS);
    }
}