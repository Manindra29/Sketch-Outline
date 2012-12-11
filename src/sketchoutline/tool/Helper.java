/**
 * Sketch Outline provides a tree view of a sketch and its 
 * members functions, variables and inner classes. Clicking on any 
 * node moves the cursor to its definition.
 *
 * Copyright (c) 2012 Manindra Moharana
 *
 * ##copyright##
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA  02111-1307  USA
 *  
 * 
 * @author	Manindra Moharana	##author##
 * @modified 19-03-2012			##date##
 * @version 0.1.5 (beta)		##version##
 */
package sketchoutline.tool;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;

/**
 * Helper functions for reading files. Modified and used from
 * japa.parser.ast.test.Helper
 * 
 * @author Julio Vilmar Gesser
 */
@SuppressWarnings("deprecation")
final class Helper {

    private Helper() {
        // hide the constructor
    }

    private static File getFile(String sourceFolder, Class<?> clazz) {
        return new File(sourceFolder, clazz.getName().replace('.', '/') + ".java");
    }

    public static CompilationUnit parserClass(String sourceFolder, Class<?> clazz) throws ParseException {
        try {
            return JavaParser.parse(getFile(sourceFolder, clazz));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static CompilationUnit parserString(String source) throws ParseException {
//    	CompilationUnit c=null;
//		try {
//			c = JavaParser.parse(new StringBufferInputStream(source));
//		} catch (Exception e) {
//			System.out.println("Exception caught in Helper.parseString " + e.toString());
//			e.printStackTrace();
//			throw (new ParseException("Parse Error."));
//			
//		} 
//		catch(Error ee){
//			System.err.println("Error caught in Helper.parseString " + ee.toString());
//			ee.printStackTrace();	
//		}
        return JavaParser.parse(new StringBufferInputStream(source));
    }

    public static String readFile(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        try {
            StringBuilder ret = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                ret.append(line);
                ret.append("\n");
            }
            return ret.toString();
        } finally {
            reader.close();
        }
    }
    
    public static String readFile(String path) throws IOException {
       return readFile(new File(path));
    }

    public static String readClass(String sourceFolder, Class<?> clazz) throws IOException {
        return readFile(getFile(sourceFolder, clazz));
    }

}
