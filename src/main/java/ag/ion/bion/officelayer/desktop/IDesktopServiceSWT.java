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
 * Last changes made by $Author: markus $, $Date: 2006-12-08 13:07:44 +0100 (Fr, 08 Dez 2006) $
 */
package ag.ion.bion.officelayer.desktop;

import org.eclipse.swt.widgets.Composite;

/**
 * Desktop service of OpenOffice.org.
 * 
 * @author Andreas Bröker
 * @author Markus Krüger
 * @version $Revision: 11158 $
 */
public interface IDesktopServiceSWT extends IDesktopService {

    // ----------------------------------------------------------------------------
    /**
     * Constructs new OpenOffice.org frame which is integrated into the submitted SWT container. This method works only
     * on local OpenOffice.org applications.
     * 
     * @param container SWT container to be used
     * @return new OpenOffice.org frame which is integrated into the submitted SWT container
     * @throws DesktopException if the frame can not be constructed
     * @author Thorsten Behrens
     */
    public IFrame constructNewOfficeFrame(Composite composite) throws DesktopException;

}
