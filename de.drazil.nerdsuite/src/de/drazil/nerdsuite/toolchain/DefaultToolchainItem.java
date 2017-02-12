package de.drazil.nerdsuite.toolchain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.exec.OS;
import org.eclipse.swt.widgets.Display;

public class DefaultToolchainItem implements IToolchainItem<Object>
{
	private String name;
	private List<String> parameters;
	private boolean isRunning = false;

	public DefaultToolchainItem(String name, String... parameters)
	{
		this(name, Arrays.asList(parameters));
	}

	public DefaultToolchainItem(String name, List<String> parameters)
	{
		this.name = name;
		this.parameters = patchStartCommand(parameters);
	}

	@Override
	public void execute()
	{
		Display.getDefault().syncExec(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					isRunning = true;
					ProcessBuilder processbuilder = new ProcessBuilder(parameters);
					Process process = processbuilder.start();
					BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
					String line = null;
					System.out.println("----------------------------------------------------");
					System.out.println("Running ToolChainItem: " + name);
					while (null != (line = br.readLine()))
					{
						System.out.println(line);
						System.out.flush();
					}
					br.close();

					int errorCode = process.waitFor();
					if (errorCode == 1)
					{
						line = null;
						br = new BufferedReader(new InputStreamReader(process.getErrorStream()));
						System.out.println("Error on ToolChainItem: " + name);
						while (null != (line = br.readLine()))
						{
							System.out.println(line);
							System.out.flush();
						}
						br.close();
					}
					isRunning = false;
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
		});

	}

	@Override
	public boolean isRunning()
	{
		return isRunning;
	}

	@Override
	public Object getResult()
	{
		// TODO Auto-generated method stub
		return null;
	}

	private List<String> patchStartCommand(List<String> parameters)
	{
		if (parameters != null && parameters.size() > 0)
		{
			for (int i = 0; i < parameters.size(); i++)
			{
				String argument = parameters.get(i);
				if (OS.isFamilyMac() && argument.endsWith(".app"))
				{
					parameters.add(i, "open");
					i += 1;
					parameters.add(i + 1, "--args");
				}
			}
		}
		return parameters;
	}
}
