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

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;

import processing.app.*;
import processing.app.Toolkit;
import sketchoutline.tool.TreeMaker.TmNode;

/**
 * The Main GUI of the Sketch Outline Tool.
 */
@SuppressWarnings("serial")
public class SketchOutlineFrame extends JFrame {

	private JPanel contentPane;
	private JScrollPane scrollPane;
	public JTree tree;
	// TreeMaker treeMaker = null;
	ThreadedTreeMaker thTreeMaker = null;
	Editor editor;
	int offset;
	TreePath lastpath;
	private JCheckBox chkbxAutoUpdate;
	private JButton btnRefresh;
	boolean okToShowFrame = false;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// TreeMaker tm = new TreeMaker(TreeMaker.PATH);
					UIManager.setLookAndFeel(UIManager
							.getSystemLookAndFeelClassName());
					SketchOutlineFrame frame = new SketchOutlineFrame(null);
					frame.setVisible(true);
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	JFrame thisFrame = this;

	/**
	 * Constructor for SketchOutlineFrame
	 */
	public SketchOutlineFrame(Editor edt) {
		this.editor = edt;
		Toolkit.setIcon(this);

		if (thTreeMaker == null) {

			if (editor != null) {

				thTreeMaker = new ThreadedTreeMaker(this, editor);

			} else {
				System.err.println("Editor is null");
				thTreeMaker = new ThreadedTreeMaker(this, null);
			}
		}

		prepareFrame();
	}

	/**
	 * Create the JFrame.
	 */
	public void prepareFrame() {

		// if (thTreeMaker.treeMaker.basicMode) {
		// System.out.println("Sketch Outline can't be used in BASIC mode.");
		// System.out
		// .println("For more info, visit: http://processing.org/reference/environment/");
		// okToShowFrame = false;
		// dispose();
		// return;
		// }

		if (thTreeMaker.treeMaker.treeCreated) {

			tree = new JTree(thTreeMaker.getTree());
			tree.setCellRenderer(new TehRenderer());
			// treeMaker.offSet - 1;
			tree.setExpandsSelectedPaths(true);
			if (editor != null)
				thTreeMaker.treeMaker.sourceString = editor.getSketch()
						.getCurrentCode().getProgram();
			else
				thTreeMaker.treeMaker.sourceString = "";

			okToShowFrame = true;
		} else {
			System.err.println("Outline couldn't be created :( "
					+ "\nThe sketch may have compilation errors.");
			if (TreeMaker.debugMode)
				System.err.println("Parsing error - SketchOutlineFrame.");
			okToShowFrame = false;
		}
		setTitle(thTreeMaker.treeMaker.mainClassName + " - Outline");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// [width=234,height=376]

		setBounds(new Rectangle(100, 100, 260, 420));
		if (editor != null) {

			setLocation(new Point(editor.getLocation().x + editor.getWidth(),
					editor.getLocation().y));
		}
		setMinimumSize(new Dimension(260, 420));
		setFocusable(true);

		// TODO: Use a proper swing layout like Box, etc. Absolute layout is a
		// messy technique
		contentPane = new JPanel();
		contentPane.setBounds(new Rectangle(0, 0, 244, 382));
		// contentPane.setBounds(0, 0, 260, 420);
		// contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		scrollPane = new JScrollPane();
		// scrollPane.setBounds(0, 0, 260, 420);
		scrollPane.setViewportView(tree);
		scrollPane.setBounds(0, 37, 244, 345);
		contentPane.add(scrollPane);

		btnRefresh = new JButton("");
		btnRefresh.setToolTipText("Refresh Outline");

		btnRefresh.setIcon(new ImageIcon(("data\\icons\\refresh_icon.png")));

		btnRefresh.setBounds(6, 4, 33, 29);
		contentPane.add(btnRefresh);

		chkbxAutoUpdate = new JCheckBox("Auto");
		chkbxAutoUpdate.setToolTipText("Auto Refresh");
		chkbxAutoUpdate.setSelected(true);
		chkbxAutoUpdate.setBounds(45, 7, 68, 23);
		contentPane.add(chkbxAutoUpdate);

		btnSortTree = new JToggleButton("");
		btnSortTree.setToolTipText("Display Alphabetically");
		btnSortTree.setIcon(new ImageIcon(("data\\icons\\sort_icon.png")));
		// sortTree.setIcon(new ImageIcon("data/a to z icon.png"));
		btnSortTree.setBounds(134, 4, 33, 29);
		contentPane.add(btnSortTree);

		btnAbout = new JButton("");
		btnAbout.setToolTipText("About Sketch Outline");

		btnAbout.setBounds(208, 4, 33, 29);
		btnAbout.setIcon(new ImageIcon(("data\\icons\\info_icon.png")));
		contentPane.add(btnAbout);

		btnShowFields = new JToggleButton("");
		btnShowFields.setToolTipText("Hide/Show Fields");
		btnShowFields.setBounds(171, 4, 33, 29);
		contentPane.add(btnShowFields);
		btnShowFields.setIcon(new ImageIcon("data\\icons\\field_icon.png"));
		/*
		 * data folder of the tool doesn't seem to import the required resource
		 * files(icons). So retrieving it from
		 * sketchbook\tools\SketchOutline\src.
		 * 
		 * I know it ain't text book style, but just works.
		 */
		if (editor != null) {
			String iconPath = (editor.getBase().getSketchbookFolder()
					.getAbsolutePath())

					+ File.separator
					+ "tools"
					+ File.separator
					+ "SketchOutline"
					+ File.separator + "data" + File.separator + "icons";
			File ic = new File(iconPath + File.separator + "refresh_icon.png");
			btnRefresh.setIcon(new ImageIcon(ic.getAbsolutePath()));

			ic = new File(iconPath + File.separator + "sort_icon.png");
			btnSortTree.setIcon(new ImageIcon(ic.getAbsolutePath()));

			ic = new File(iconPath + File.separator + "field_icon.png");
			btnShowFields.setIcon(new ImageIcon(ic.getAbsolutePath()));

			ic = new File(iconPath + File.separator + "info_icon.png");
			btnAbout.setIcon(new ImageIcon(ic.getAbsolutePath()));
		}
		// iListen
		addListeners();

		// Start the auto update thread.
		thTreeMaker.start();
	}

	private void scrollToLocation() {
		try {

			if (thisFrame.hasFocus())
				return;
			DefaultMutableTreeNode n = (DefaultMutableTreeNode) tree
					.getLastSelectedPathComponent();
			if (n == null)
				return;
			// thTreeMaker.lastpath = event.getPath();
			if (lastpath != null)
				thTreeMaker.lastRow = (tree.getRowForPath(lastpath));
			//
			// System.out.println("tree select: " + ",, "
			// + thTreeMaker.lastRow);
			TmNode tn = (TmNode) n.getUserObject();
			// System.out.println("Clicked on: " + tn.label +
			// " - "
			// + tn.node.getBeginLine());

			offset = thTreeMaker.treeMaker.xyToOffset(tn.node.getBeginLine()
					- thTreeMaker.treeMaker.mainClassLineOffset,
					tn.node.getBeginColumn());
			// =
			// editor.getTextArea().xyToOffset(tn.node.getBeginLine()-
			// thTreeMaker.treeMaker.mainOffSet,
			// tn.node.getBeginColumn());

			// System.out.print("Line no: "
			// + (tn.node.getBeginLine() -
			// thTreeMaker.treeMaker.mainClassLineOffset));
			// + "," + tn.node.getBeginColumn());
			// System.out.println("Calculated Offset: " +
			// offset);
			//
			// System.out.println("Editor offset: "
			// + editor.getCaretOffset());

			if (editor.getCaretOffset() != offset) {
				// System.out.println("offset unequal");
				editor.toFront();
				editor.setSelection(offset, offset);
				editor.getTextArea().repaint();
			} else {
				// System.out.println("Offset fine");
			}

		} catch (Exception ex) {
			System.out.println("Error positioning cursor." + ex.toString());
			if (TreeMaker.debugMode)
				ex.printStackTrace();
		}
	}

	DockTool2Base Docker = new DockTool2Base();
	private JToggleButton btnSortTree;
	private JButton btnAbout;
	private JToggleButton btnShowFields;
	public boolean listenForTreeExpand = true;
	public boolean listenForSelection = true;

	/**
	 * Adds {@link ComponentListener} and {@link WindowListener} listeners to
	 * SketchOutline frame and the Editor frame. Used for implementing the
	 * docking feature. Adds other action listeners for all components like
	 * clicking on tree node scrolls to its definition, button presses, etc.
	 */
	private void addListeners() {

		btnSortTree.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				thTreeMaker.treeMaker.enableSortingCodeTree = btnSortTree
						.isSelected();
				if (!thTreeMaker.updateTree())
					System.out
							.println("Tree couldn't be rebuilt, the sketch may have compilation errors.");

			}
		});

		btnShowFields.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				thTreeMaker.treeMaker.hideFields = !thTreeMaker.treeMaker.hideFields;
				if (!thTreeMaker.updateTree())
					System.out
							.println("Tree couldn't be rebuilt, the sketch may have compilation errors.");
			}
		});

		btnRefresh.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				thTreeMaker.updateTree();

				// Hold down shift key and click refresh to enable debug
				// messages.
				if (e.getModifiers() != KeyEvent.VK_SHIFT) {
					TreeMaker.debugMode = !TreeMaker.debugMode;
					if (TreeMaker.debugMode) {
						System.err.println("Sketch Outline Debug Mode ON");

					} else {
						System.out.println("Sketch Outline Debug Mode OFF");
					}
					System.out
							.println("Hold down shift key and click refresh to toggle debug messages.");
				}
			}
		});

		chkbxAutoUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chkbxAutoUpdate.isSelected())
					thTreeMaker.start();
				else
					thTreeMaker.halt();
			}
		});

		contentPane.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {

			}

			@Override
			public void componentResized(ComponentEvent e) {

				Dimension d = e.getComponent().getSize();
				if (d.width >= 244 && d.height >= 382) {
					scrollPane.setSize(d.width, d.height - scrollPane.getY());
					scrollPane.validate();
				}
			}

			@Override
			public void componentMoved(ComponentEvent e) {

			}

			@Override
			public void componentHidden(ComponentEvent e) {

			}
		});

		btnAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// 'bout me.
				System.out
						.println("===========================================================");
				System.out.println();
				System.out
						.println("                Sketch Outline 0.1.7 (beta)                ");
				System.out
						.println("                  By - Manindra Moharana                   ");
				System.out
						.println("              http://www.mkmoharana.com/              ");
				System.out.println();
				System.out
						.println("===========================================================");
				System.out
						.println("<> Project page: https://github.com/Manindra29/Sketch-Outline");
				System.out
						.println("<> Processing Forum Thread: \nhttp://forum.processing.org/topic/sketch-outline-new-processing-tool-announcement");
				System.out
						.println("<> Please report any bugs in the issues section of the project page or forum discussion thread.");
				System.out
						.println("<> Sketch Outline Debug Mode - Hold down shift key and click refresh.");
			}
		});

		tree.addTreeExpansionListener(new TreeExpansionListener() {

			@SuppressWarnings("rawtypes")
			@Override
			public void treeExpanded(final TreeExpansionEvent e) {
				SwingWorker worker = new SwingWorker() {

					@Override
					protected Object doInBackground() throws Exception {
						return null;
					}

					protected void done() {
						if (!listenForTreeExpand)
							return;
						// System.out.println("Expand: " + e.getPath() + " , "
						// + tree.getRowForPath(e.getPath()));
						if (tree.getRowForPath(e.getPath()) >= 0)
							thTreeMaker.lastExpandedRows.add(tree
									.getRowForPath(e.getPath()));
					}
				};
				worker.execute();
			}

			@SuppressWarnings("rawtypes")
			@Override
			public void treeCollapsed(final TreeExpansionEvent e) {
				SwingWorker worker = new SwingWorker() {

					@Override
					protected Object doInBackground() throws Exception {
						return null;
					}

					protected void done() {
						if (!listenForTreeExpand)
							return;
						// System.out.println("Collapse: " + e.getPath() + " , "
						// + tree.getRowForPath(e.getPath()));
						if (tree.getRowForPath(e.getPath()) >= 0)
							thTreeMaker.lastExpandedRows.remove(tree
									.getRowForPath(e.getPath()));
					}
				};
				worker.execute();
			}
		});

		// The next set of listerners are needed only if a PDE is around
		if (editor == null)
			return;
		/**
		 * Scroll to definition happens here
		 */
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(final TreeSelectionEvent event) {
				@SuppressWarnings("rawtypes")
				SwingWorker worker = new SwingWorker() {

					@Override
					protected Object doInBackground() throws Exception {
						return null;
					}

					protected void done() {
						lastpath = event.getPath();
						scrollToLocation();
					}
				};
				worker.execute();
			}
		});

		/*
		 * tree.addMouseListener(new MouseListener() {
		 * 
		 * @Override public void mouseReleased(MouseEvent e) {
		 * 
		 * }
		 * 
		 * @Override public void mousePressed(MouseEvent e) {
		 * 
		 * }
		 * 
		 * @Override public void mouseExited(MouseEvent e) {
		 * 
		 * }
		 * 
		 * @Override public void mouseEntered(MouseEvent e) {
		 * 
		 * }
		 * 
		 * 
		 * @SuppressWarnings("rawtypes")
		 * 
		 * @Override public void mouseClicked(final MouseEvent event) {
		 * 
		 * SwingWorker worker = new SwingWorker() {
		 * 
		 * @Override protected Object doInBackground() throws Exception { return
		 * null; }
		 * 
		 * protected void done() { //scrollToLocation(); } }; worker.execute();
		 * } });
		 */

		editor.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {

			}

			@Override
			public void componentResized(ComponentEvent e) {
				if (Docker.isDocked()) {
					Docker.dock();
				} else {
					Docker.tryDocking();
				}
			}

			@Override
			public void componentMoved(ComponentEvent e) {

				if (Docker.isDocked()) {
					Docker.dock();
				} else {
					Docker.tryDocking();
				}

			}

			@Override
			public void componentHidden(ComponentEvent e) {
				System.out.println("ed hidden");
			}
		});

		editor.addWindowListener(new WindowListener() {

			@Override
			public void windowClosing(WindowEvent e) {
				thisFrame.dispose();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				thisFrame.dispose();
			}

			@Override
			public void windowIconified(WindowEvent e) {
				thisFrame.setExtendedState(Frame.ICONIFIED);
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				thisFrame.setExtendedState(Frame.NORMAL);
			}

			@Override
			public void windowActivated(WindowEvent e) {
				if (e.getOppositeWindow() != thisFrame) {
					thisFrame.requestFocus();
					editor.requestFocus();
				}
			}

			@Override
			public void windowOpened(WindowEvent e) {

			}

			@Override
			public void windowDeactivated(WindowEvent e) {

			}

		});

		thisFrame.addComponentListener(new ComponentListener() {

			@Override
			public void componentShown(ComponentEvent e) {

			}

			@Override
			public void componentResized(ComponentEvent e) {
				Docker.tryDocking();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				Docker.tryDocking();
			}

			@Override
			public void componentHidden(ComponentEvent e) {

			}
		});

		thisFrame.addWindowListener(new WindowListener() {

			public void windowOpened(WindowEvent e) {

			}

			@Override
			public void windowClosing(WindowEvent e) {
				thisFrame.dispose();
			}

			@Override
			public void windowClosed(WindowEvent e) {
				thisFrame.dispose();
			}

			@Override
			public void windowIconified(WindowEvent e) {

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				editor.setExtendedState(Editor.NORMAL);
			}

			@Override
			public void windowActivated(WindowEvent e) {
				if (e.getOppositeWindow() != editor) {
					editor.requestFocus();
					thisFrame.requestFocus();
				}
			}

			@Override
			public void windowDeactivated(WindowEvent e) {

			}
		});
	}

	public void dispose() {
		thTreeMaker.halt();
		setVisible(false);
	}

	private class TehRenderer extends DefaultTreeCellRenderer {

		// ImageIcon icons[];
		private final ImageIcon classIcon, fieldIcon, methodIcon;

		public TehRenderer() {
			// icons = new ImageIcon[15];
			// File f = new File("data" + File.separator + "icons");
			String iconPath = "data" + File.separator + "icons";
			if (editor != null) {
				iconPath = (Base.getSketchbookFolder().getAbsolutePath())

				+ File.separator + "tools" + File.separator + "SketchOutline"
						+ File.separator + "data" + File.separator + "icons";
				;
			}
			// File[] iconfiles = f.listFiles();
			// if (iconfiles.length != 15)
			// System.err
			// .println("Icon files have been tamepered with. Zomg!");
			// for (int i = 0; i < icons.length; i++) {
			// icons[i] = new ImageIcon(iconfiles[i].getAbsolutePath());
			// }

			classIcon = new ImageIcon(iconPath + File.separator
					+ "class_obj.png");
			methodIcon = new ImageIcon(iconPath + File.separator
					+ "methpub_obj.png");
			fieldIcon = new ImageIcon(iconPath + File.separator
					+ "field_default_obj.png");
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {

			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);
			if (value instanceof DefaultMutableTreeNode)
				setIcon(getTreeIcon(value));
			else {
				System.out.println("Weird: "
						+ value.getClass().getCanonicalName());
			}

			return this;
		}

		public javax.swing.Icon getTreeIcon(Object o) {
			if (o instanceof DefaultMutableTreeNode) {

				TmNode tmnode = (TmNode) ((DefaultMutableTreeNode) o)
						.getUserObject();
				String type = TreeMaker.getType(tmnode.node);
				if (type.equals("MethodDeclaration")
						|| type.equals("ConstructorDeclaration"))
					return methodIcon;
				if (type.equals("ClassOrInterfaceDeclaration"))
					return classIcon;
				if (type.equals("FieldDeclaration"))
					return fieldIcon;
			}

			return null;

		}

	}

	/**
	 * Implements the docking feature of the tool - The frame sticks to the
	 * editor and once docked, moves along with it as the editor is resized,
	 * moved, maximized, minimized or closed.
	 * 
	 * This class has been borrowed from Tab Manager tool by Thomas Diewald. It
	 * has been slightly modified and used here.
	 * 
	 * @author: Thomas Diewald
	 */
	private class DockTool2Base {

		private int docking_border = 0;
		private int dock_on_editor_y_offset_ = 0;
		private int dock_on_editor_x_offset_ = 0;

		// ///////////////////////////////
		// ____2____
		// | |
		// | |
		// 0 | editor | 1
		// | |
		// |_________|
		// 3
		// ///////////////////////////////

		public void reset() {
			dock_on_editor_y_offset_ = 0;
			dock_on_editor_x_offset_ = 0;
			docking_border = 0;
		}

		public boolean isDocked() {
			return (docking_border >= 0);
		}

		private final int MAX_GAP_ = 20;

		//
		public void tryDocking() {
			Editor editor = thTreeMaker.treeMaker.editor;
			Frame frame = thisFrame;

			int ex = editor.getX();
			int ey = editor.getY();
			int ew = editor.getWidth();
			int eh = editor.getHeight();

			int fx = frame.getX();
			int fy = frame.getY();
			int fw = frame.getWidth();
			int fh = frame.getHeight();

			if (((fy > ey) && (fy < ey + eh))
					|| ((fy + fh > ey) && (fy + fh < ey + eh))) {
				int dis_border_left = Math.abs(ex - (fx + fw));
				int dis_border_right = Math.abs((ex + ew) - (fx));

				if (dis_border_left < MAX_GAP_ || dis_border_right < MAX_GAP_) {
					docking_border = (dis_border_left < dis_border_right) ? 0
							: 1;
					dock_on_editor_y_offset_ = fy - ey;
					dock();
					return;
				}
			}

			if (((fx > ex) && (fx < ex + ew))
					|| ((fx + fw > ey) && (fx + fw < ex + ew))) {
				int dis_border_top = Math.abs(ey - (fy + fh));
				int dis_border_bot = Math.abs((ey + eh) - (fy));

				if (dis_border_top < MAX_GAP_ || dis_border_bot < MAX_GAP_) {
					docking_border = (dis_border_top < dis_border_bot) ? 2 : 3;
					dock_on_editor_x_offset_ = fx - ex;
					dock();
					return;
				}
			}
			docking_border = -1;
		}

		public void dock() {

			Editor editor = thTreeMaker.treeMaker.editor;
			Frame frame = thisFrame;

			int ex = editor.getX();
			int ey = editor.getY();
			int ew = editor.getWidth();
			int eh = editor.getHeight();

			int fx = frame.getX();
			int fy = frame.getY();
			int fw = frame.getWidth();
			int fh = frame.getHeight();

			int x = 0, y = 0;
			if (docking_border == -1) {
				return;
			}

			if (docking_border == 0) {
				x = ex - fw;
				y = ey + dock_on_editor_y_offset_;
			}
			if (docking_border == 1) {
				x = ex + ew;
				y = ey + dock_on_editor_y_offset_;
			}

			if (docking_border == 2) {
				x = ex + dock_on_editor_x_offset_;
				y = ey - fh;
			}
			if (docking_border == 3) {
				x = ex + dock_on_editor_x_offset_;
				y = ey + eh;
			}
			frame.setLocation(x, y);
		}
	}
}
