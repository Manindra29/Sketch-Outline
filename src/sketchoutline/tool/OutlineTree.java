package sketchoutline.tool;

import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RenderDataProvider;
import org.netbeans.swing.outline.RowModel;

import processing.app.Base;

import sketchoutline.tool.TreeMaker.TmNode;
import sun.reflect.generics.tree.Tree;

public class OutlineTree extends Outline {
	public static void main(String[] args) {
		System.out.println(123);
	}

	RenderTehIcons renderer;

	public OutlineTree(DefaultMutableTreeNode rootNode) {
		TreeModel treeModel = new DefaultTreeModel(rootNode);
		OutlineModel outlineModel = DefaultOutlineModel.createOutlineModel(
				treeModel, new MyRowModel());
		setModel(outlineModel);
		renderer = new RenderTehIcons();
		setRenderDataProvider(renderer);
	}

	public void updateTree(DefaultMutableTreeNode rootNode) {
		TreeModel treeModel = new DefaultTreeModel(rootNode);
		OutlineModel outlineModel = DefaultOutlineModel.createOutlineModel(
				treeModel, new MyRowModel());
		setModel(outlineModel);
	}

	private class MyRowModel implements RowModel {

		public Class getColumnClass(int column) {
			return null;
		}

		public int getColumnCount() {
			return 0;
		}

		public String getColumnName(int column) {
			return null;
		}

		public Object getValueFor(Object node, int column) {
			return null;
		}

		public boolean isCellEditable(Object node, int column) {
			return false;
		}

		public void setValueFor(Object node, int column, Object value) {
		}

	}

	private class RenderTehIcons implements RenderDataProvider {

		ImageIcon icons[];

		public RenderTehIcons() {
			icons = new ImageIcon[10];
			File f = new File("data" + File.separator + "icons");
			if(!f.exists())
			{
				String iconPath = (Base.getSketchbookFolder()
						.getAbsolutePath())

						+ File.separator
						+ "tools"
						+ File.separator
						+ "SketchOutline"
						+ File.separator + "data" + File.separator + "icons";
				f = new File(iconPath); 
			}
			File[] iconfiles = f.listFiles();
			if (iconfiles.length != 15)
				System.err
						.println("Icon files have been tamepered with. Zomg!");
			for (int i = 0; i < icons.length; i++) {
				icons[i] = new ImageIcon(iconfiles[i].getAbsolutePath());
			}
		}

		public java.awt.Color getBackground(Object o) {
			return null;
		}

		public String getDisplayName(Object o) {
			if (o instanceof DefaultMutableTreeNode)
				return ((DefaultMutableTreeNode) o).toString();
			else
				return "default";
		}

		public java.awt.Color getForeground(Object o) {
			return null;
		}

		public javax.swing.Icon getIcon(Object o) {
			if (o instanceof DefaultMutableTreeNode) {

				TmNode tmnode = (TmNode) ((DefaultMutableTreeNode) o)
						.getUserObject();
				String type = TreeMaker.getType(tmnode.node);
				if (type.equals("MethodDeclaration") || type.equals("ConstructorDeclaration"))
					return icons[9];
				if (type.equals("ClassOrInterfaceDeclaration"))
					return icons[0];
				if (type.equals("FieldDeclaration"))
					return icons[1];
			}
			
			return null;

		}

		public String getTooltipText(Object o) {
			return null;
		}

		public boolean isHtmlDisplayName(Object o) {
			return false;
		}

	}

}
