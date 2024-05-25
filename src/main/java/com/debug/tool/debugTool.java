package com.debug.tool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.debug.db.record.recordProcesses;
import com.debug.db.record.recordTable;
import com.debug.device.interconnectionManager;
import com.debug.utils.globalSymbols;
import com.debug.utils.logger;
import com.debug.utils.simpleFileProcess;
import com.google.common.base.Splitter;

public class debugTool extends Thread {

	private static debugTool DebugTool;

	private Boolean isRunning = false;

	recordProcesses recprocesses;

	interconnectionManager InterconnectionManager;

	public debugTool(Map<String, String> tparams) {
		if ( //
		tparams.containsKey(globalSymbols.Port1str) || //
				tparams.containsKey(globalSymbols.Baudrate1str) || //
				tparams.containsKey(globalSymbols.StopBits1str) || //
				tparams.containsKey(globalSymbols.DataBits1str) || //
				tparams.containsKey(globalSymbols.Parity1str) || //
				tparams.containsKey(globalSymbols.Port2str) || //
				tparams.containsKey(globalSymbols.Baudrate2str) || //
				tparams.containsKey(globalSymbols.StopBits2str) || //
				tparams.containsKey(globalSymbols.DataBits2str) || //
				tparams.containsKey(globalSymbols.Parity2str) //
		) {
			globalSymbols.Port1Name = tparams.get(globalSymbols.Port1str);
			globalSymbols.Baudrate1 = Integer.valueOf(tparams.get(globalSymbols.Baudrate1str));
			globalSymbols.StopBits1 = Integer.valueOf(tparams.get(globalSymbols.StopBits1str));
			globalSymbols.DataBits1 = Integer.valueOf(tparams.get(globalSymbols.DataBits1str));
			globalSymbols.Parity1 = tparams.get(globalSymbols.Parity1str);
			globalSymbols.Port2Name = tparams.get(globalSymbols.Port2str);
			globalSymbols.Baudrate2 = Integer.valueOf(tparams.get(globalSymbols.Baudrate2str));
			globalSymbols.StopBits2 = Integer.valueOf(tparams.get(globalSymbols.StopBits2str));
			globalSymbols.DataBits2 = Integer.valueOf(tparams.get(globalSymbols.DataBits2str));
			globalSymbols.Parity2 = tparams.get(globalSymbols.Parity2str);

			InterconnectionManager = new interconnectionManager();
		}

		if (InterconnectionManager.openports()) {
			recprocesses = new recordProcesses();
		}
	}

	public static void main(String[] args) {
		simpleFileProcess.ensureFolder(globalSymbols.dbFolder);
		Map<String, String> params;
		if (args.length > 0) {
			params = Splitter.on(";").withKeyValueSeparator("=").split(args[0]);
			DebugTool = new debugTool(params);
			DebugTool.isRunning = true;
			DebugTool.start();
		} else {
			logger.DebugLogger(debugTool.class, "No input parameter!!!");
			return;
		}
	}

	@Override
	public void run() {
		super.run();
		while (isRunning) {
			try {
				String dwmSerialData = InterconnectionManager.PORT1.getData();
				String espSerialData = InterconnectionManager.PORT2.getData();

				if (dwmSerialData != null) {
					recordTable table = new recordTable(globalSymbols.Port2Name, globalSymbols.Port1Name, dwmSerialData,
							new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
					recprocesses.insert(table);
					InterconnectionManager.PORT2.send(dwmSerialData);
				}

				if (espSerialData != null) {
					recordTable table = new recordTable(globalSymbols.Port1Name, globalSymbols.Port2Name, espSerialData,
							new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
					recprocesses.insert(table);
					InterconnectionManager.PORT1.send(espSerialData);
//					InterconnectionManager.checkAndSend(espSerialData);
				}

				Thread.sleep(1);
			} catch (InterruptedException e) {
				logger.ErrorLogger(debugTool.class, e);
			}
		}
	}
}
