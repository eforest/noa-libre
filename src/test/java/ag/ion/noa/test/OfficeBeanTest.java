/**
 * **************************************************************************
 *                                                                          *
 * NOA (Nice Office Access) *
 * ------------------------------------------------------------------------ * *
 * The Contents of this file are made available subject to * the terms of GNU
 * Lesser General Public License Version 2.1. * * GNU Lesser General Public
 * License Version 2.1 *
 * ======================================================================== *
 * Copyright 2003-2006 by IOn AG * * This library is free software; you can
 * redistribute it and/or * modify it under the terms of the GNU Lesser General
 * Public * License version 2.1, as published by the Free Software Foundation. *
 * * This library is distributed in the hope that it will be useful, * but
 * WITHOUT ANY WARRANTY; without even the implied warranty of * MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU * Lesser General Public
 * License for more details. * * You should have received a copy of the GNU
 * Lesser General Public * License along with this library; if not, write to the
 * Free Software * Foundation, Inc., 59 Temple Place, Suite 330, Boston, * MA
 * 02111-1307 USA * * Contact us: * http://www.ion.ag * http://ubion.ion.ag *
 * info@ion.ag * *
 * **************************************************************************
 */
/*
 * Last changes made by $Author: andreas $, $Date: 2006-10-04 14:14:28 +0200 (Mi, 04 Okt 2006) $
 */
package ag.ion.noa.test;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.Panel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import ag.ion.bion.officelayer.application.IApplicationAssistant;
import ag.ion.bion.officelayer.application.ILazyApplicationInfo;
import ag.ion.bion.officelayer.application.IOfficeApplication;
import ag.ion.bion.officelayer.application.OfficeApplicationException;
import ag.ion.bion.officelayer.application.OfficeApplicationRuntime;
import ag.ion.bion.officelayer.desktop.IFrame;
import ag.ion.bion.officelayer.document.DocumentDescriptor;
import ag.ion.bion.officelayer.document.IDocument;
import ag.ion.bion.officelayer.filter.PDFFilter;
import ag.ion.bion.officelayer.internal.application.ApplicationAssistant;
import junit.framework.TestCase;

/**
 * Test case for the OpenOffice.org Bean.
 *
 * @author Andreas Bröker
 * @version $Revision: 10398 $
 */
public class OfficeBeanTest extends TestCase {

    private static Logger LOGGER = Logger.getLogger( "ag.ion" );

    private IDocument document = null;
    private File file = null;

    // ----------------------------------------------------------------------------
    /**
     * Main entry point for the OpenOffice.org Bean Test.
     *
     * @param args arguments of the test
     * @author Andreas Bröker
     * @date 21.05.2006
     */
    public static void main(String[] args) throws OfficeApplicationException {

        LogManager.getLogManager().reset();
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel( Level.FINEST );
        LOGGER.addHandler( consoleHandler );
        LOGGER.setLevel( Level.FINEST );

        try {
            FileHandler fileHandler = new FileHandler( "log.xml" );
            fileHandler.setLevel( Level.FINEST );
            LOGGER.addHandler( fileHandler );
        }
        catch ( Throwable throwable ) {
        }
        OfficeBeanTest testOfficeBean = new OfficeBeanTest();

        if ( args.length == 0 ) {
            testOfficeBean.test( null );
        }
        else if ( args.length == 1 ) {
            testOfficeBean.test( args[0] );
        }
        else if ( args.length == 4 ) {
            testOfficeBean
                          .remoteTestPDF(
                              new File( args[2] ),
                              new File( args[3] ),
                              args[0],
                              Integer.valueOf( args[1] ) );
        }
        else {
            System.out.println( "usage:\nOfficeBeanTest host port source-odt target-pdf\nOfficeBeanTest officeHome" );
        }
    }

    // ----------------------------------------------------------------------------
    /**
     * Test OpenOffice.org Bean.
     *
     * @author Andreas Bröker
     * @param home
     * @throws ag.ion.bion.officelayer.application.OfficeApplicationException
     * @date 21.05.2006
     */
    public void testOfficeBean(String home) throws OfficeApplicationException {
        OfficeBeanTest testOfficeBean = new OfficeBeanTest();
        System.out.println( "testOfficeBean: " + home );
        testOfficeBean.test( home );
    }

    public void remoteTestPDF(File source, File target, String host, int port) {
        Map<String, String> configuration = new HashMap<String, String>();
        configuration.put( IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.REMOTE_APPLICATION );
        configuration.put( IOfficeApplication.APPLICATION_HOST_KEY, host.replace( "http://", "" ) );
        configuration.put( IOfficeApplication.APPLICATION_PORT_KEY, String.valueOf( port ) );
        configuration.put( IOfficeApplication.APPLICATION_ARGUMENTS_KEY, String.valueOf( port ) );
        System.out.println( "Office host: " + host );

        try {
            System.out.println( "Activating OpenOffice.org connection ..." );
            final IOfficeApplication application = OfficeApplicationRuntime.getApplication( configuration );
            application.activate();

            System.out.println( "Document stream to pdf.." );
            application
                       .getDocumentService()
                       .loadDocument( source.getAbsolutePath() )
                       .getPersistenceService()
                       .export( new FileOutputStream( target ), PDFFilter.FILTER );
            System.out.println( "Document export to pdf done. " + target.getCanonicalPath() );

            document.close();
            if ( document.isOpen() ) {
                document.close();
            }
            try {
                System.out.println( "Deactivating Office connection ..." );
                application.deactivate();
            }
            catch ( OfficeApplicationException applicationException ) {
            }
        }
        catch ( Throwable throwable ) {
            throwable.printStackTrace();
            fail( throwable.getMessage() );
        }
        System.out.println( "NOA Office Bean Test successfully." );
    }

    // ----------------------------------------------------------------------------
    /**
     * Test the OpenOffice.org Bean by creating an empty odt file, opening it, creating a pdf and cleanup
     *
     * @param officeHome home path to OpenOffice.org
     * @author Andreas Bröker
     * @date 21.05.2006
     */
    public void test(String officeHome) throws OfficeApplicationException {
        System.out.println( "NOA Office Bean Test" );

        if ( officeHome == null ) {
            IApplicationAssistant applicationAssistant = new ApplicationAssistant();
            ILazyApplicationInfo appInfo = applicationAssistant.getLatestLocalOpenOfficeOrgApplication();
            if ( appInfo == null ) {
                appInfo = applicationAssistant.getLatestLocalLibreOfficeApplication();
            }
            if ( appInfo == null ) {
                throw new IllegalStateException( "No OO/LO found." );
            }
            System.out.println( appInfo.getClass() + " - Office major version:" + appInfo.getMajorVersion() );

            officeHome = appInfo.getHome();
        }
        System.out.println( "Office home: " + officeHome );
        HashMap hashMap = new HashMap( 2 );
        hashMap.put( IOfficeApplication.APPLICATION_TYPE_KEY, IOfficeApplication.LOCAL_APPLICATION );
        hashMap.put( IOfficeApplication.APPLICATION_HOME_KEY, officeHome );
        hashMap.put( IOfficeApplication.APPLICATION_PORT_KEY, "8100" );

        try {
            System.out.println( "Activating OpenOffice.org connection ..." );
            final IOfficeApplication application = OfficeApplicationRuntime.getApplication( hashMap );
            application.activate();
            final Frame frame = new Frame();
            frame.setVisible( true );
            frame.setSize( 400, 400 );
            frame.validate();
            Panel panel = new Panel( new BorderLayout() );
            frame.add( panel );
            panel.setVisible( true );
            frame.addWindowListener( new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    frame.dispose();
                    document.close();
                    file.delete();
                    try {
                        System.out.println( "Deactivating OpenOffice.org connection ..." );
                        application.deactivate();
                    }
                    catch ( OfficeApplicationException applicationException ) {
                    }
                }
            } );

            System.out.println( "Constructing document for test ..." );
            IFrame officeFrame = application.getDesktopService().constructNewOfficeFrame( panel );
            officeFrame.setFocus();
            document = application.getDocumentService().constructNewHiddenDocument( IDocument.WRITER );
            System.out.println( "Document for test constructed." );
            file = new File( "OfficeBeanTest.odt" );
            document.getPersistenceService().store( new FileOutputStream( file ) );
            document.close();
            System.out.println( "Loading document for test ..." );
            document = application
                                  .getDocumentService()
                                  .loadDocument( officeFrame, new FileInputStream( file ), new DocumentDescriptor() );
            System.out.println( "Document for test loaded." );

            System.out.println( "Document export to pdf.." );
            PDFFilter pdfFilter = PDFFilter.FILTER;
            File pdf = new File( "OfficeBeanTestPdfa.pdf" );
            pdf.delete();
            // FIXME commented out is special for pdf/A, will become part of noalibre
            // PropertyValue[] filterData = new PropertyValue[1];
            // filterData[0] = new PropertyValue();
            // filterData[0].Name = "SelectPdfVersion";
            // filterData[0].Value = new Integer(1); //0: normal 1.4, 1: PDF/A
            //
            // String filterDefinition = pdfFilter.getFilterDefinition(document);
            // PropertyValue[] properties = new PropertyValue[2];
            // properties[0] = new PropertyValue();
            // properties[0].Name = "FilterName"; //$NON-NLS-1$
            // properties[0].Value = filterDefinition;
            // properties[1] = new PropertyValue();
            // properties[1].Name = "FilterData";
            // properties[1].Value = filterData;
            //

            // URL url = pdf.toURI().toURL();
            // XStorable xStorable = UnoRuntime.queryInterface(XStorable.class, document.getXComponent());
            // xStorable.storeToURL(url.toString(), properties);
            application
                       .getDocumentService()
                       .loadDocument( file.getAbsolutePath() )
                       .getPersistenceService()
                       .export( pdf.getAbsolutePath(), pdfFilter );
            System.out.println( "Document export to pdf done. " + pdf.getCanonicalPath() );

            frame.validate();
            officeFrame.getXFrame().getController().suspend( true );
            document.close();

            frame.dispose();
            if ( document.isOpen() ) {
                document.close();
            }
            file.delete();
            pdf.delete();
            try {
                System.out.println( "Deactivating Office connection ..." );
                application.deactivate();
            }
            catch ( OfficeApplicationException applicationException ) {
            }
        }
        catch ( Throwable throwable ) {
            throwable.printStackTrace();
            fail( throwable.getMessage() );
        }
        System.out.println( "NOA Office Bean Test successfully." );
    }
    // ----------------------------------------------------------------------------

}
