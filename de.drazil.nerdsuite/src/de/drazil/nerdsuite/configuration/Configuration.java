package de.drazil.nerdsuite.configuration;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

import de.drazil.nerdsuite.Constants;
import de.drazil.nerdsuite.assembler.InstructionSet;
import de.drazil.nerdsuite.model.Workspace;

public class Configuration
{
	public final static File WORKSPACE_PATH = new File(Constants.USER_HOME + Constants.FILE_SEPARATOR + Constants.DEFAULT_WORKSPACE_NAME);
	private Workspace workspace;
	private Font font = null;

	public final Workspace getWorkspace()
	{
		return createWorkspace();
	}

	public void initialize()
	{
		try
		{

			workspace = getWorkspace();
			InstructionSet.init(Constants.PLUGIN_BASE_PATH);

			Bundle bundle = Platform.getBundle("de.drazil.nerdsuite");
			URL fileURL = bundle.getEntry("/fonts/C64_Pro_Mono-STYLE.ttf");
			File file = null;
			boolean b;
			try
			{
				file = new File(FileLocator.resolve(fileURL).toURI());
				b = Display.getCurrent().loadFont(file.toString());
			}
			catch (URISyntaxException e1)
			{
				e1.printStackTrace();
			}
			catch (IOException e1)
			{
				e1.printStackTrace();
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private final Workspace createWorkspace()
	{
		try
		{
			if (!WORKSPACE_PATH.exists())
			{
				System.out.println("create new workspace folder...");
				WORKSPACE_PATH.mkdir();
				workspace = new Workspace();
				writeWorkspace(workspace);
			}
			else
			{
				if (workspace == null)
				{
					System.out.println("read workspace file...");
					JAXBContext jaxbContext = JAXBContext.newInstance(Workspace.class);
					Unmarshaller jaxbMarschaller = jaxbContext.createUnmarshaller();
					workspace = (Workspace) jaxbMarschaller.unmarshal(new File(WORKSPACE_PATH + Constants.FILE_SEPARATOR + ".projects"));
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return workspace;
	}

	public final void writeWorkspace(Workspace workspace)
	{
		try
		{
			JAXBContext jaxbContext = JAXBContext.newInstance(Workspace.class);
			Marshaller jaxbMarschaller = jaxbContext.createMarshaller();
			jaxbMarschaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			jaxbMarschaller.marshal(workspace, new File(WORKSPACE_PATH + Constants.FILE_SEPARATOR + ".projects"));
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
	}
}
