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

import java.awt.EventQueue;
import javax.swing.JFrame;
import processing.app.*;
import processing.app.tools.*;

/**
 * The main class for the tool.
 */
public class SketchOutline implements Tool {

	Editor editor;

	public String getMenuTitle() {
		return "Sketch Outline";
	}

	public void init(Editor theEditor) {
		editor = theEditor;

	}

	SketchOutlineFrame frame;
	TreeMaker treemaker;

	public void run() {

		System.out.println("Sketch Outline 0.1.7 (beta)");
		System.out.println("By - Manindra Moharana | http://www.mkmoharana.com/");
		String mode = editor.getMode().getTitle();
		if (mode.equals("Android") || mode.equals("JavaScript")) {
			System.out
					.println("This tool is still in beta and hasn't been tested throughly in "
							+ mode
							+ " mode. Please report any bugs in the issues section of the project page.");
		}
		try {

			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						if (frame == null || frame.thTreeMaker.treeMaker.basicMode) {
							frame = new SketchOutlineFrame(editor);							
						}						
						
						
						if (frame.okToShowFrame)						
							frame.setVisible(true);
						frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

					} catch (Exception e) {

						if (TreeMaker.debugMode) {
							System.err.println("Exception at Tool.run()");
							e.printStackTrace();
						}
					}
				}
			});
		} catch (Exception e2) {
			System.err.println("Exception at Tool.run() - invokeLater");
			if (TreeMaker.debugMode)
				e2.printStackTrace();
		}

	}

}
