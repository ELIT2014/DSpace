package org.dspace.app.webui.submit.step;

import org.dspace.app.util.SubmissionInfo;
import org.dspace.app.webui.submit.JSPStep;
import org.dspace.app.webui.submit.JSPStepManager;
import org.dspace.authorize.AuthorizeException;
import org.dspace.core.Context;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

public class JSPDisableStep extends JSPStep {
    /**
     * JSP which displays HTML for this Class *
     */
    private static final String DISPLAY_JSP = "/submit/disable-step.jsp";

    /**
     * JSP which reviews information gathered by DISPLAY_JSP *
     */
    private static final String REVIEW_JSP = "/submit/disable-step.jsp";

    public JSPDisableStep() throws ServletException {
        super();
    }

    public void doPreProcessing(Context context, HttpServletRequest request,
                                HttpServletResponse response, SubmissionInfo subInfo)
            throws ServletException, IOException, SQLException,
            AuthorizeException {
        JSPStepManager.showJSP(request, response, subInfo, DISPLAY_JSP);
    }


    public void doPostProcessing(Context context, HttpServletRequest request,
                                 HttpServletResponse response, SubmissionInfo subInfo, int status)
            throws ServletException, IOException, SQLException,
            AuthorizeException {
    }


    private void showEditMetadata(Context context, HttpServletRequest request,
                                  HttpServletResponse response, SubmissionInfo subInfo)
            throws SQLException, ServletException, IOException {
    }

    public String getReviewJSP(Context context, HttpServletRequest request,
                               HttpServletResponse response, SubmissionInfo subInfo) {
        return REVIEW_JSP;
    }
}
