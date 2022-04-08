package ag.ion.bion.officelayer.internal.desktop;

import org.eclipse.swt.widgets.Composite;

import com.sun.star.frame.XDesktop;
import com.sun.star.frame.XFrame;

import ag.ion.bion.officelayer.application.connection.IOfficeConnection;
import ag.ion.bion.officelayer.desktop.DesktopException;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.internal.application.connection.LocalOfficeConnection;
import ag.ion.bion.officelayer.internal.application.connection.LocalOfficeConnectionSWT;

public class DesktopServiceSWT extends DesktopService {

    public DesktopServiceSWT(XDesktop xDesktop, IOfficeConnection officeConnection) throws IllegalArgumentException {
        super( xDesktop, officeConnection );
    }

    public DesktopServiceSWT(XDesktop xDesktop, IOfficeConnection officeConnection, boolean preventTermination)
        throws IllegalArgumentException {
        super( xDesktop, officeConnection, preventTermination );
    }

    /**
     * Constructs new OpenOffice.org frame which is integrated into the submitted SWT container. This method works only
     * on local OpenOffice.org applications.
     * 
     * @param container SWT container to be used
     * @return new OpenOffice.org frame which is integrated into the submitted SWT container
     * @throws DesktopException if the frame can not be constructed
     * @author Thorsten Behrens
     */
    public IFrame constructNewOfficeFrame(Composite composite) throws DesktopException {
        IOfficeConnection officeConnection = getOfficeConnection();
        if ( officeConnection instanceof LocalOfficeConnection ) {
            LocalOfficeConnectionSWT officeConnectionSWT =
                new LocalOfficeConnectionSWT( (LocalOfficeConnection) officeConnection );
            XFrame xFrame = officeConnectionSWT.getOfficeFrame( composite );
            Frame frame = new Frame( xFrame, officeConnection );
            return frame;
        }
        throw new DesktopException( "New frames can only constructed for local OpenOffice.org applications." );
    }
}
