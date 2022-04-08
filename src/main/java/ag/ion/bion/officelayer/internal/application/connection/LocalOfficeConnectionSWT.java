package ag.ion.bion.officelayer.internal.application.connection;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.widgets.Composite;

import com.sun.star.awt.XSystemChildFactory;
import com.sun.star.awt.XToolkit;
import com.sun.star.awt.XWindow;
import com.sun.star.awt.XWindowPeer;
import com.sun.star.frame.XFrame;
import com.sun.star.lang.SystemDependent;
import com.sun.star.uno.UnoRuntime;

public class LocalOfficeConnectionSWT {

    private static Logger LOGGER = Logger.getLogger( LocalOfficeConnection.class.getName() );

    private LocalOfficeConnection officeConnection;

    public LocalOfficeConnectionSWT(LocalOfficeConnection officeConnection) {
        this.officeConnection = officeConnection;
    }

    // ----------------------------------------------------------------------------
    /**
     * Returns OpenOffice.org frame integrated into the submitted Java SWT container.
     * 
     * @param container java SWT container
     * @return OpenOffice.org frame integrated into the submitted Java SWT container
     * @author Thorsten Behrens
     */
    public XFrame getOfficeFrame(final Composite container) {
        if ( officeConnection.hasOfficeConnection() ) {
            try {
                // TODO needs to be changed in later version as the dispose listener can be used.
                if ( !officeConnection.isConnected() )
                    officeConnection.openConnection();

                if ( LOGGER.isLoggable( Level.FINEST ) )
                    LOGGER.finest( "Creating local office window." );

                XToolkit xToolkit =
                    (XToolkit) UnoRuntime
                                         .queryInterface(
                                             XToolkit.class,
                                             officeConnection
                                                             .getXMultiServiceFactory()
                                                             .createInstance( "com.sun.star.awt.Toolkit" ) );

                // initialise le xChildFactory
                XSystemChildFactory xChildFactory =
                    (XSystemChildFactory) UnoRuntime.queryInterface( XSystemChildFactory.class, xToolkit );

                long handle = container.handle;
                byte[] procID = new byte[0];

                XWindowPeer xWindowPeer =
                    xChildFactory.createSystemChild( (Object) handle, procID, SystemDependent.SYSTEM_WIN32 );

                XWindow xWindow = (XWindow) UnoRuntime.queryInterface( XWindow.class, xWindowPeer );

                Object object = officeConnection.getXMultiServiceFactory().createInstance( "com.sun.star.frame.Task" ); //$NON-NLS-1$
                if ( object == null )
                    object = officeConnection.getXMultiServiceFactory().createInstance( "com.sun.star.frame.Frame" ); //$NON-NLS-1$
                if ( LOGGER.isLoggable( Level.FINEST ) )
                    LOGGER.finest( "Creating UNO XFrame interface." );
                XFrame xFrame = (XFrame) UnoRuntime.queryInterface( XFrame.class, object );
                xFrame.getContainerWindow();
                xFrame.initialize( xWindow );
                xFrame.setName( xFrame.toString() );
                if ( LOGGER.isLoggable( Level.FINEST ) )
                    LOGGER.finest( "Creating desktop service." );
                Object desktop =
                    officeConnection.getXMultiServiceFactory().createInstance( "com.sun.star.frame.Desktop" ); //$NON-NLS-1$
                com.sun.star.frame.XFrames xFrames =
                    ( (com.sun.star.frame.XFramesSupplier) UnoRuntime
                                                                     .queryInterface(
                                                                         com.sun.star.frame.XFramesSupplier.class,
                                                                         desktop ) ).getFrames();
                xFrames.append( xFrame );
                return xFrame;
            }
            catch ( Exception exception ) {
                LOGGER.throwing( this.getClass().getName(), "getOfficeFrame", exception );
                // exception.printStackTrace();
                return null;
            }
        }
        else {
            return null;
        }
    }
}
