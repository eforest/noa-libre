/****************************************************************************
 * ubion.ORS - The Open Report Suite                                        *
 *                                                                          *
 * ------------------------------------------------------------------------ *
 *                                                                          *
 * Subproject: NOA (Nice Office Access)                                     *
 *                                                                          *
 *                                                                          *
 * The Contents of this file are made available subject to                  *
 * the terms of GNU Lesser General Public License Version 2.1.              *
 *                                                                          * 
 * GNU Lesser General Public License Version 2.1                            *
 * ======================================================================== *
 * Copyright 2003-2005 by IOn AG                                            *
 *                                                                          *
 * This library is free software; you can redistribute it and/or            *
 * modify it under the terms of the GNU Lesser General Public               *
 * License version 2.1, as published by the Free Software Foundation.       *
 *                                                                          *
 * This library is distributed in the hope that it will be useful,          *
 * but WITHOUT ANY WARRANTY; without even the implied warranty of           *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU        *
 * Lesser General Public License for more details.                          *
 *                                                                          *
 * You should have received a copy of the GNU Lesser General Public         *
 * License along with this library; if not, write to the Free Software      *
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,                    *
 * MA  02111-1307  USA                                                      *
 *                                                                          *
 * Contact us:                                                              *
 *  http://www.ion.ag                                                       *
 *  info@ion.ag                                                             *
 *                                                                          *
 ****************************************************************************/

/*
 * Last changes made by $Author: markus $, $Date: 2010-07-13 12:08:38 +0200 (Di, 13 Jul 2010) $
 */
package ag.ion.bion.officelayer.internal.application.connection;

import java.awt.Container;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import com.sun.star.comp.beans.ContainerFactory;
import com.sun.star.comp.beans.LocalOfficeWindow;
import com.sun.star.comp.beans.OfficeConnection;
import com.sun.star.comp.beans.OfficeWindow;
import com.sun.star.lang.XEventListener;
import com.sun.star.lib.uno.helper.UnoUrl;
import com.sun.star.lib.util.NativeLibraryLoader;
import com.sun.star.uno.XComponentContext;

import ag.ion.bion.officelayer.OSHelper;
import ag.ion.bion.officelayer.runtime.IOfficeProgressMonitor;
import ooo.connector.BootstrapConnector;
import ooo.connector.server.OOoServer;

/**
 * Office connection implementation. This class bases on the implementation of the OpenOffice.org API Bean package.
 * 
 * @author Andreas Bröker
 * @author Markus Krüger
 * @version $Revision: 11754 $
 */
public class LocalOfficeConnectionGhost implements OfficeConnection {

    // TODO: adapt to modifications of OpenOffice.org !

    private static final String OFFICE_APP_NAME = "soffice"; //$NON-NLS-1$
    private static final String OFFICE_LIB_NAME = "officebean"; //$NON-NLS-1$
    private static final String OFFICE_ID_SUFFIX = "_Office"; //$NON-NLS-1$

    private static boolean msvcrLoaded = false;
    private static boolean uwinapiLoaded = false;
    private static boolean jawtLoaded = false;
    // private static boolean jpipeLoaded = false;
    private static boolean officebeanLoaded = false;

    private static final Random randomPipeName = new Random();

    private String programPath = null;
    private String[] officeArguments = null;

    private BootstrapConnector bootstrapConnector = null;
    private XComponentContext context = null;

    private String pipe = null;
    private String port = null;

    private List<XEventListener> components = new Vector<>();

    private OfficeConnectionWrapper officeConnectionWrapper = null;
    private IOfficeProgressMonitor officeProgressMonitor = null;

    // ----------------------------------------------------------------------------
    /**
     * Internal office connection wrapper.
     * 
     * @author Andreas Bröker
     */
    private class OfficeConnectionWrapper implements OfficeConnection {

        // ----------------------------------------------------------------------------
        /**
         * Sets a connection URL. This implementation accepts a UNO URL with following format:<br>
         * 
         * <pre>
         *  url    := uno:localoffice[,&lt;params&gt;];urp;StarOffice.ServiceManager
         *  params := &lt;path&gt;[,&lt;pipe&gt;]
         *  path   := path=&lt;pathv&gt;
         *  pipe   := pipe=&lt;pipev&gt;
         *  pathv  := platform_specific_path_to_the_local_office_distribution
         *  pipev  := local_office_connection_pipe_name
         * </pre>
         * 
         * @param url this is UNO URL which discribes the type of a connection
         * @throws MalformedURLException if the URL is not valid
         * @author Andreas Bröker
         */
        public void setUnoUrl(String url) throws MalformedURLException {
            LocalOfficeConnectionGhost.this.setUnoUrl( url );
        }

        // ----------------------------------------------------------------------------
        /**
         * Sets an AWT container catory.
         * 
         * @param containerFactory this is a application provided AWT container factory
         * @author Andreas Bröker
         */
        public void setContainerFactory(ContainerFactory containerFactory) {
            LocalOfficeConnectionGhost.this.setContainerFactory( containerFactory );
        }

        // ----------------------------------------------------------------------------
        /**
         * Retrives the UNO component context. Establishes a connection if necessary and initialises the UNO service
         * manager if it has not already been initialised. This method can return <code>null</code> if it fails to
         * connect to the office application.
         * 
         * @return the office UNO component context
         * @author Andreas Bröker
         */
        public XComponentContext getComponentContext() {
            return LocalOfficeConnectionGhost.this.getComponentContext();
        }

        // ----------------------------------------------------------------------------
        /**
         * Creates an office window. The window is either a sub-class of java.awt.Canvas (local) or java.awt.Container
         * (RVP).
         * 
         * @param container this is an AWT container
         * @return the office window instance
         * @author Andreas Bröker
         */
        public OfficeWindow createOfficeWindow(Container container) {
            return createOfficeWindow();
        }
        // ----------------------------------------------------------------------------

        public OfficeWindow createOfficeWindow() {
            return LocalOfficeConnectionGhost.this.createOfficeWindow();
        }

        // ----------------------------------------------------------------------------
        /**
         * Closes the connection.
         * 
         * @author Andreas Bröker
         */
        public void dispose() {
            LocalOfficeConnectionGhost.this.dispose();
        }

        // ----------------------------------------------------------------------------
        /**
         * Adds an event listener to the object.
         * 
         * @param eventListener is a listener object
         * @author Andreas Bröker
         */
        public void addEventListener(XEventListener eventListener) {
            LocalOfficeConnectionGhost.this.addEventListener( eventListener );
        }

        // ----------------------------------------------------------------------------
        /**
         * Removes an event listener from the listener list.
         * 
         * @param eventListener is a listener object
         * @author Andreas Bröker
         */
        public void removeEventListener(XEventListener eventListener) {
            LocalOfficeConnectionGhost.this.removeEventListener( eventListener );
        }
        // ----------------------------------------------------------------------------

    }

    // ----------------------------------------------------------------------------

    // ----------------------------------------------------------------------------
    /**
     * Internal local office window wrapper.
     * 
     * @author Andreas Bröker
     */
    private class LocalOfficeWindowWrapper extends LocalOfficeWindow {

        private static final long serialVersionUID = 1L;

        // ----------------------------------------------------------------------------
        /**
         * Constructs new LocalOfficeWindowWrapper.
         * 
         * @param officeConnection office connection to be used
         * @author Andreas Bröker
         */
        protected LocalOfficeWindowWrapper(OfficeConnection officeConnection) {
            super( officeConnection );
        }
        // ----------------------------------------------------------------------------

    }

    // ----------------------------------------------------------------------------

    // ----------------------------------------------------------------------------
    /**
     * Constructs new LocalOfficeConnectionGhost. Sets up paths to the office application and native libraries if values
     * are available in <code>OFFICE_PROP_FILE</code> in the user home directory.<br>
     * "com.sun.star.beans.path" - the office application directory;<br>
     * "com.sun.star.beans.libpath" - native libraries directory.
     * 
     * @param officeProgressMonitor office progress monitor to be used (can be null)
     * @author Andreas Bröker
     */
    public LocalOfficeConnectionGhost(IOfficeProgressMonitor officeProgressMonitor) {
        // lazy load dlls
        // loadNativeLibraries();
        this.officeProgressMonitor = officeProgressMonitor;
        try {
            setUnoUrl(
                "uno:pipe,name=" + getPipeName() //$NON-NLS-1$
                    + ";urp;StarOffice.ServiceManager" ); //$NON-NLS-1$
        }
        catch ( java.net.MalformedURLException malformedURLException ) {
            throw new RuntimeException( malformedURLException );
        }
    }

    // ----------------------------------------------------------------------------
    /**
     * Sets a connection URL. This implementation accepts a UNO URL with following format:<br>
     * 
     * <pre>
     *  url    := uno:localoffice[,&lt;params&gt;];urp;StarOffice.ServiceManager
     *  params := &lt;path&gt;[,&lt;pipe&gt;]
     *  path   := path=&lt;pathv&gt;
     *  pipe   := pipe=&lt;pipev&gt;
     *  pathv  := platform_specific_path_to_the_local_office_distribution
     *  pipev  := local_office_connection_pipe_name
     * </pre>
     * 
     * @param url this is UNO URL which discribes the type of a connection
     * @throws MalformedURLException if the URL is not valid
     * @author Andreas Bröker
     */
    public void setUnoUrl(String url) throws java.net.MalformedURLException {
        String prefix = "uno:localoffice"; //$NON-NLS-1$
        if ( url.startsWith( prefix ) )
            parseUnoUrlWithOfficePath( url, prefix );
        else {
            try {
                UnoUrl aURL = UnoUrl.parseUnoUrl( url );
                programPath = null;
                pipe = (String) aURL.getConnectionParameters().get( "pipe" ); //$NON-NLS-1$
                port = (String) aURL.getConnectionParameters().get( "port" ); //$NON-NLS-1$
            }
            catch ( com.sun.star.lang.IllegalArgumentException illegalArgumentException ) {
                throw new java.net.MalformedURLException( "Invalid UNO connection URL." ); //$NON-NLS-1$
            }
        }
    }

    /**
     * Set office connection port
     * 
     * @param port office connection port
     */
    public void setPort(String port) {
        this.port = port;
    }

    // ----------------------------------------------------------------------------
    /**
     * Sets arguments for OpenOffice.org.
     * 
     * @param arguments path to OpenOffice.org installation
     * @author Markus Krüger
     * @date 09.08.2010
     */
    public void setOfficeArguments(String[] arguments) {
        this.officeArguments = arguments;
    }

    // ----------------------------------------------------------------------------
    /**
     * Sets an AWT container factory.
     * 
     * @param containerFactory this is a application provided AWT container factory
     * @author Andreas Bröker
     */
    public void setContainerFactory(ContainerFactory containerFactory) {
        // don't use container factory
    }

    // ----------------------------------------------------------------------------
    /**
     * Retrives the UNO component context. Establishes a connection if necessary and initialises the UNO service manager
     * if it has not already been initialised. This method can return <code>null</code> if it fails to connect to the
     * office application.
     * 
     * @return the office UNO component context
     * @author Andreas Bröker
     */
    synchronized public XComponentContext getComponentContext() {
        if ( officeProgressMonitor != null )
            if ( officeProgressMonitor.isCanceled() )
                return null;

        if ( context == null )
            context = connect();
        return context;
    }

    // ----------------------------------------------------------------------------
    /**
     * Retrives the UNO component context. If no context is set, null will be returned. There will be no try to connect.
     * 
     * @return the office UNO component context
     * @author Markus Krüger
     */
    synchronized public XComponentContext getCurrentComponentContext() {
        if ( officeProgressMonitor != null )
            if ( officeProgressMonitor.isCanceled() )
                return null;
        return context;
    }

    // ----------------------------------------------------------------------------
    /**
     * Creates an office window. The window is either a sub-class of java.awt.Canvas (local) or java.awt.Container
     * (RVP).
     * 
     * @return the office window instance
     * @author Andreas Bröker
     */
    public OfficeWindow createOfficeWindow() {
        if ( officeConnectionWrapper == null )
            officeConnectionWrapper = new OfficeConnectionWrapper();
        return new LocalOfficeWindowWrapper( officeConnectionWrapper );
    }

    public OfficeWindow createOfficeWindow(Container container) {
        return createOfficeWindow();
    }

    // ----------------------------------------------------------------------------
    /**
     * Closes the connection.
     * 
     * @author Andreas Bröker
     */
    public void dispose() {
        Iterator<XEventListener> iterator = components.iterator();
        while ( iterator.hasNext() ) {
            try {
                ( iterator.next() ).disposing( null );
            }
            catch ( RuntimeException runtimeException ) {
                // do not consume
            }
        }
        components.clear();

        if ( bootstrapConnector != null ) {
            bootstrapConnector.disconnect();
            bootstrapConnector = null;
        }

        context = null;
    }

    // ----------------------------------------------------------------------------
    /**
     * Adds an event listener to the object.
     * 
     * @param eventListener is a listener object
     * @author Andreas Bröker
     */
    public void addEventListener(XEventListener eventListener) {
        if ( eventListener == null )
            return;
        components.add( eventListener );
    }

    // ----------------------------------------------------------------------------
    /**
     * Removes an event listener from the listener list.
     * 
     * @param eventListener is a listener object
     * @author Andreas Bröker
     */
    public void removeEventListener(XEventListener eventListener) {
        if ( eventListener == null )
            return;
        components.remove( eventListener );
    }

    // ----------------------------------------------------------------------------
    /**
     * Establishes the connection to the office.
     * 
     * @return constructed component context
     * @author Andreas Bröker
     */
    @SuppressWarnings( "unchecked" )
    private XComponentContext connect() {
        loadNativeLibraries();

        try {
            if ( officeProgressMonitor != null )
                officeProgressMonitor
                                     .beginSubTask(
                                         Messages
                                                 .getString(
                                                     "LocalOfficeConnectionGhost_monitor_constructing_initial_context_message" ) ); //$NON-NLS-1$
            OOoServer oooServer = null;
            String programPath = getProgramPath();
            boolean isLibreOffice = isLibreOffice( programPath );

            if ( officeArguments != null && officeArguments.length > 0 ) {
                oooServer = new OOoServer( programPath, Arrays.asList( officeArguments ) );
            }
            else {
                List<String> defaultOOoOptions = OOoServer.getDefaultOOoOptions();
                if ( isLibreOffice ) {
                    List<String> defaultLibreOfficeOptions = new ArrayList<>();
                    for ( int i = 0, n = defaultOOoOptions.size(); i < n; i++ ) {
                        defaultLibreOfficeOptions.add( "-" + defaultOOoOptions.get( i ) );
                    }
                    defaultOOoOptions = defaultLibreOfficeOptions;
                }
                oooServer = new OOoServer( programPath, defaultOOoOptions );
            }
            bootstrapConnector = new BootstrapConnector( oooServer );

            XComponentContext xContext = null;

            if ( this.port != null && !this.port.isEmpty() ) {
                // try connecting by port
                String host = "localhost";
                String hostAndPort = "host=" + host + ",port=" + port;
                String oooAcceptOption = "-accept=socket," + hostAndPort + ";urp;";
                if ( isLibreOffice ) {
                    oooAcceptOption = "-" + oooAcceptOption;
                }
                String unoConnectString = "uno:socket," + hostAndPort + ";urp;StarOffice.ComponentContext";

                System.out.println( "Connect to Office with: " + oooAcceptOption + " / " + unoConnectString );
                xContext = bootstrapConnector.connect( oooAcceptOption, unoConnectString );
            }
            else {

                if ( this.pipe == null || this.pipe.isEmpty() ) {
                    pipe = getPipeName();
                }

                // accept option
                String oooAcceptOption = "-accept=pipe,name=" + pipe + ";urp;";
                if ( isLibreOffice ) {
                    oooAcceptOption = "-" + oooAcceptOption;
                }

                // connection string
                String unoConnectString = "uno:pipe,name=" + pipe + ";urp;StarOffice.ComponentContext";

                System.out.println( "Connect to Office with: " + oooAcceptOption + " / " + unoConnectString );
                xContext = bootstrapConnector.connect( oooAcceptOption, unoConnectString );
            }

            return xContext;
        }
        catch ( com.sun.star.uno.RuntimeException exception ) {
            System.out.println( "--- RuntimeException:" ); //$NON-NLS-1$
            System.out.println( exception.getMessage() );
            exception.printStackTrace();
            System.out.println( "--- end." ); //$NON-NLS-1$
            throw exception;
        }
        catch ( java.lang.Exception exception ) {
            System.out.println( "java.lang.Exception: " ); //$NON-NLS-1$
            System.out.println( exception );
            exception.printStackTrace();
            System.out.println( "--- end." ); //$NON-NLS-1$
            throw new com.sun.star.uno.RuntimeException( exception.toString() );
        }
    }

    // ----------------------------------------------------------------------------
    /**
     * Returns if the program path seems to point to a libre office installation.
     * 
     * @param programPath The path to analyse.
     * @return if the program path seems to point to a libre office installation
     * @author Markus KrÃ¼ger
     */
    private boolean isLibreOffice(String programPath) {
        return programPath != null && programPath.toLowerCase().indexOf( "libre" ) > -1;
    }

    // ----------------------------------------------------------------------------
    /**
     * Retrieves a path to the office program folder.
     * 
     * @return the path to the office program folder
     * @author Andreas Bröker
     */
    private String getProgramPath() {
        if ( programPath == null ) {
            String officeHomePath = System.getProperty( "office.home" ); //$NON-NLS-1$
            if ( officeHomePath != null ) {
                if ( OSHelper.IS_MAC )
                    officeHomePath = officeHomePath + "/Contents/MacOS";
                return officeHomePath + File.separator + "program"; //$NON-NLS-1$
            }
            // determine name of executable soffice
            String exec = OFFICE_APP_NAME; // default for UNIX and mac

            // running on Windows?
            if ( OSHelper.IS_WINDOWS )
                exec = OFFICE_APP_NAME + ".exe"; //$NON-NLS-1$

            // add other non-UNIX operating systems here
            // ...

            File path = NativeLibraryLoader.getResource( LocalOfficeConnection.class.getClassLoader(), exec );
            if ( path != null )
                programPath = path.getParent();

            if ( programPath == null )
                programPath = ""; //$NON-NLS-1$
        }
        return programPath;
    }

    // ----------------------------------------------------------------------------
    /**
     * Parses a connection URL. This method accepts a UNO URL with following format:<br />
     * 
     * <pre>
     *  url    := uno:localoffice[,&lt;params&gt;];urp;StarOffice.NamingService
     *  params := &lt;path&gt;[,&lt;pipe&gt;]
     *  path   := path=&lt;pathv&gt;
     *  pipe   := pipe=&lt;pipev&gt;
     *  pathv  := platform_specific_path_to_the_local_office_distribution
     *  pipev  := local_office_connection_pipe_name
     * </pre>
     * 
     * <h4>Examples</h4>
     * <ul>
     * <li>"uno:localoffice,pipe=xyz_Office,path=/opt/openoffice11/program;urp;StarOffice.ServiceManager" ;
     * <li>"uno:socket,host=localhost,port=8100;urp;StarOffice.ServiceManager";
     * </ul>
     * 
     * @param url this is UNO URL which describes the type of a connection
     * @param prefix prefix to be used
     * @exception java.net.MalformedURLException when inappropreate URL was provided
     * @author OpenOffice.org
     */
    private void parseUnoUrlWithOfficePath(String url, String prefix) throws java.net.MalformedURLException {
        // Extruct parameters.
        int index = url.indexOf( ";urp;StarOffice.NamingService" ); //$NON-NLS-1$
        if ( index < 0 )
            throw new java.net.MalformedURLException( "Invalid UNO connection URL." ); //$NON-NLS-1$
        String params = url.substring( prefix.length(), index + 1 );

        // Parse parameters.
        String name = null;
        String path = null;
        String pipe = null;
        char ch;
        int state = 0;
        StringBuffer buffer = new StringBuffer();
        for ( index = 0; index < params.length(); index += 1 ) {
            ch = params.charAt( index );
            switch ( state ) {
                case 0: // initial state
                    switch ( ch ) {
                        case ',':
                            buffer.delete( 0, buffer.length() );
                            state = 1;
                            break;

                        case ';':
                            state = 7;
                            break;

                        default:
                            buffer.delete( 0, buffer.length() );
                            buffer.append( ch );
                            state = 1;
                            break;
                    }
                    break;

                case 1: // parameter name
                    switch ( ch ) {
                        case ' ':
                        case '=':
                            name = buffer.toString();
                            state = ( ch == ' ' ) ? 2 : 3;
                            break;

                        case ',':
                        case ';':
                            state = -6; // error: invalid name
                            break;

                        default:
                            buffer.append( ch );
                            break;
                    }
                    break;

                case 2: // equal between the name and the value
                    switch ( ch ) {
                        case '=':
                            state = 3;
                            break;

                        case ' ':
                            break;

                        default:
                            state = -1; // error: missing '='
                            break;
                    }
                    break;

                case 3: // value leading spaces
                    switch ( ch ) {
                        case ' ':
                            break;

                        default:
                            buffer.delete( 0, buffer.length() );
                            buffer.append( ch );
                            state = 4;
                            break;
                    }
                    break;

                case 4: // value
                    switch ( ch ) {
                        case ' ':
                        case ',':
                        case ';':
                            index -= 1; // put back the last read character
                            state = 5;
                            if ( name.equals( "path" ) ) { //$NON-NLS-1$
                                if ( path == null )
                                    path = buffer.toString();
                                else
                                    state = -3; // error: more then one 'path'
                            }
                            else if ( name.equals( "pipe" ) ) { //$NON-NLS-1$
                                if ( pipe == null )
                                    pipe = buffer.toString();
                                else
                                    state = -4; // error: more then one 'pipe'
                            }
                            else
                                state = -2; // error: unknown parameter
                            buffer.delete( 0, buffer.length() );
                            break;

                        default:
                            buffer.append( ch );
                            break;
                    }
                    break;

                case 5: // a delimeter after the value
                    switch ( ch ) {
                        case ' ':
                            break;

                        case ',':
                            state = 6;
                            break;

                        case ';':
                            state = 7;
                            break;

                        default:
                            state = -5; // error: ' ' inside the value
                            break;
                    }
                    break;

                case 6: // leading spaces before next parameter name
                    switch ( ch ) {
                        case ' ':
                            break;

                        default:
                            buffer.delete( 0, buffer.length() );
                            buffer.append( ch );
                            state = 1;
                            break;
                    }
                    break;

                default:
                    throw new java.net.MalformedURLException( "Invalid UNO connection URL." ); //$NON-NLS-1$
            }
        }
        if ( state != 7 )
            throw new java.net.MalformedURLException( "Invalid UNO connection URL." ); //$NON-NLS-1$

        // Set up the connection parameters.
        if ( path != null )
            programPath = path;
        if ( pipe != null )
            this.pipe = pipe;
    }

    // ----------------------------------------------------------------------------
    /**
     * Replaces each substring aSearch in aString by aReplace. StringBuffer.replaceAll() is not avaialable in Java
     * 1.3.x.
     * 
     * @param aString string to be used
     * @param aSearch search string to be used
     * @param aReplace replacement to be used
     * @return converted string
     * @author OpenOffice.org
     */
    private String replaceAll(String aString, String aSearch, String aReplace) {
        StringBuffer aBuffer = new StringBuffer( aString );

        int nPos = aString.length();
        int nOfs = aSearch.length();

        while ( ( nPos = aString.lastIndexOf( aSearch, nPos - 1 ) ) > -1 )
            aBuffer.replace( nPos, nPos + nOfs, aReplace );
        return aBuffer.toString();
    }

    // ----------------------------------------------------------------------------
    /**
     * Creates a unique pipe name.
     * 
     * @return unique pipe name
     * @author OpenOffice.org
     */
    private String getPipeName() {
        // turn user name into a URL and file system safe name (% chars will not
        // work)
        String aPipeName = System.getProperty( "user.name" ) + "_" //$NON-NLS-1$ //$NON-NLS-2$
            + Long.toString( randomPipeName.nextLong() & 0x7fffffffffffffffL ) + OFFICE_ID_SUFFIX;
        aPipeName = replaceAll( aPipeName, "_", "%B7" ); //$NON-NLS-1$ //$NON-NLS-2$
        return replaceAll(
            replaceAll( java.net.URLEncoder.encode( aPipeName, StandardCharsets.UTF_8 ), "\\+", "%20" ), //$NON-NLS-1$ //$NON-NLS-2$
            "%", //$NON-NLS-1$
            "_" );  //$NON-NLS-1$
    }

    // ----------------------------------------------------------------------------
    /**
     * Loads the necessary native libraries.
     * 
     * @author Andreas Bröker
     * @date 20.03.2006
     */
    private synchronized void loadNativeLibraries() {
        String officeHomePath = System.getProperty( "office.home" ); //$NON-NLS-1$
        if ( OSHelper.IS_WINDOWS ) {
            try {
                if ( !msvcrLoaded ) {
                    if ( officeHomePath == null )
                        NativeLibraryLoader.loadLibrary( LocalOfficeConnection.class.getClassLoader(), "msvcr70" ); //$NON-NLS-1$
                    else
                        System
                              .load(
                                  officeHomePath + File.separator + "program" //$NON-NLS-1$
                                      + File.separator + System.mapLibraryName( "msvcr70" ) ); //$NON-NLS-1$
                    msvcrLoaded = true;
                }
            }
            catch ( Throwable throwable ) {
                // System.err.println("cannot find msvcr70"); //$NON-NLS-1$
            }

            try {
                if ( !msvcrLoaded ) {
                    if ( officeHomePath == null )
                        NativeLibraryLoader.loadLibrary( LocalOfficeConnection.class.getClassLoader(), "msvcr71" ); //$NON-NLS-1$
                    else
                        System
                              .load(
                                  officeHomePath + File.separator + "program" //$NON-NLS-1$
                                      + File.separator + System.mapLibraryName( "msvcr71" ) ); //$NON-NLS-1$
                    msvcrLoaded = true;
                }
            }
            catch ( Throwable throwable ) {
                // System.err.println("cannot find msvcr71"); //$NON-NLS-1$
            }

            try {
                if ( !uwinapiLoaded ) {
                    if ( officeHomePath == null )
                        NativeLibraryLoader.loadLibrary( LocalOfficeConnection.class.getClassLoader(), "uwinapi" ); //$NON-NLS-1$
                    else
                        System
                              .load(
                                  officeHomePath + File.separator + "program" //$NON-NLS-1$
                                      + File.separator + System.mapLibraryName( "uwinapi" ) ); //$NON-NLS-1$
                    uwinapiLoaded = true;
                }
            }
            catch ( Throwable throwable ) {
                // System.err.println("cannot find uwinapi"); //$NON-NLS-1$
            }

            try {
                if ( !jawtLoaded ) {
                    if ( officeHomePath == null )
                        NativeLibraryLoader.loadLibrary( LocalOfficeConnection.class.getClassLoader(), "jawt" ); //$NON-NLS-1$
                    else
                        System
                              .load(
                                  officeHomePath + File.separator + "program" //$NON-NLS-1$
                                      + File.separator + System.mapLibraryName( "jawt" ) ); //$NON-NLS-1$
                    jawtLoaded = true;
                }
            }
            catch ( Throwable throwable ) {
                // System.err.println("cannot find jawt"); //$NON-NLS-1$
            }

            // try {
            // if ( !jpipeLoaded ) {
            // if ( officeHomePath == null )
            // NativeLibraryLoader.loadLibrary( LocalOfficeConnection.class.getClassLoader(), "jpipe" ); //$NON-NLS-1$
            // else
            // System
            // .load(
            // officeHomePath + File.separator + "program" //$NON-NLS-1$
            // + File.separator + System.mapLibraryName( "jpipe" ) ); //$NON-NLS-1$
            // jpipeLoaded = true;
            // }
            // }
            // catch ( Throwable throwable ) {
            // // System.err.println("cannot find jpipe"); //$NON-NLS-1$
            // }
        }

        try {
            if ( !officebeanLoaded ) {
                if ( officeHomePath == null )
                    NativeLibraryLoader.loadLibrary( LocalOfficeConnection.class.getClassLoader(), OFFICE_LIB_NAME );
                else
                    System
                          .load(
                              officeHomePath + File.separator + "program" + File.separator //$NON-NLS-1$
                                  + System.mapLibraryName( OFFICE_LIB_NAME ) );
                officebeanLoaded = true;
            }
        }
        catch ( Throwable throwable ) {
            // do not consume
        }
    }
}
