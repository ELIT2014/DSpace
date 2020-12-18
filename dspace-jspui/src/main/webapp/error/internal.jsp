<%--

    The contents of this file are subject to the license and copyright
    detailed in the LICENSE and NOTICE files at the root of the source
    tree and available online at

    http://www.dspace.org/license/

--%>
<%--
  - Page representing an internal server error
  --%>

<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"
    prefix="fmt" %>
	
<%@ page import="java.io.PrintWriter" %>

<%@ page isErrorPage="true" %>

<%@ taglib uri="http://www.dspace.org/dspace-tags.tld" prefix="dspace" %>

<%@ page import="java.util.Locale"%>
<%@ page import="org.dspace.app.webui.util.UIUtil" %>
<%@ page import="javax.servlet.jsp.jstl.core.*" %>

<%
    Locale sessionLocale = UIUtil.getSessionLocale(request);
    Config.set(request.getSession(), Config.FMT_LOCALE, sessionLocale);
%>

<%@page import="javax.mail.*"%>

<%@page import="javax.mail.internet.*"%>
<%@page import="java.util.*"%>
<%@page import="org.apache.log4j.Logger"%>


<%
    Logger log = Logger.getLogger("internal.jsp");
    String host = "smtp.gmail.com";
    String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
    String to_add = "maksimilian199@gmail.com", subject = "Essuir error", messageText = "Need to reboot DB";

    String from = "zzzzzz";

    boolean sessionDebug = true;

    Properties props = System.getProperties();

    props.put("mail.host", host);

    props.put("mail.transport.protocol.", "smtp");

    props.put("mail.smtp.auth", "true");

    props.put("mail.smtp.", "true");

    props.put("mail.smtp.port", "465");

    props.put("mail.smtp.socketFactory.fallback", "false");

    props.put("mail.smtp.socketFactory.class", SSL_FACTORY);

    Session mailSession = Session.getDefaultInstance(props, null);

    mailSession.setDebug(sessionDebug);

    Message msg = new MimeMessage(mailSession);

    msg.setFrom(new InternetAddress(from));

    InternetAddress[] address = { new InternetAddress(to_add) };

    msg.setRecipients(Message.RecipientType.TO, address);

    msg.setSubject(subject);

    msg.setContent(messageText, "text/html"); // use setText if you want to send text

    Transport transport = mailSession.getTransport("smtp");
    System.setProperty("javax.net.ssl.trustStore", "conf/jssecacerts");
    System.setProperty("javax.net.ssl.trustStorePassword", "admin");
    transport.connect(host, "essuir.server@gmail.com", "essuirserver1");

    try {
        transport.sendMessage(msg, msg.getAllRecipients());
        log.info("Email to sergpet was sent");
    }
    catch (Exception err) {
        log.error(err);
    }
    transport.close();
%>

<dspace:layout titlekey="jsp.error.internal.title">
    <%-- <h1>Internal System Error</h1> --%>
    <h1><fmt:message key="jsp.error.internal.title"/></h1>
    <%-- <p>Oops!  The system has experienced an internal error.  This is our fault,
    please pardon our dust during these early stages of the DSpace system!</p> --%>
    <p><fmt:message key="jsp.error.internal.text1"/></p>
    <%-- <p>The system has logged this error.  Please try to do what you were doing
    again, and if the problem persists, please contact us so we can fix the
    problem.</p> --%>

    <dspace:include page="/components/contact-info.jsp" />

    <p align="center">
        <%-- <a href="<%= request.getContextPath() %>/">Go to the DSpace home page</a> --%>
        <a href="<%= request.getContextPath() %>/"><fmt:message key="jsp.general.gohome"/></a>
    </p>
        <!--
    <%
    Throwable ex = (Throwable) request.getAttribute("javax.servlet.error.exception");
    if(ex == null) out.println("No stack trace available<br/>");
    else {
                for(Throwable t = ex ; t!=null; t = t.getCause())
                {
                    out.println(t.getMessage());
                    out.println("=============================================");
                    t.printStackTrace(new PrintWriter(out));
                    out.println("\n\n\n");
                }
        }
        %>
      -->
</dspace:layout>
