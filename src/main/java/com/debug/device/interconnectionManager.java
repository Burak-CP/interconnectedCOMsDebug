package com.debug.device;

import com.debug.utils.globalSymbols;

public class interconnectionManager {

	public comDevice PORT1;
	public comDevice PORT2;

	boolean sendEnabled = false;
	int sendedDataCount = 0;

	public interconnectionManager() {
	}

	public boolean openports() {
		PORT1 = new comDevice(globalSymbols.Port1Name, globalSymbols.Baudrate1, globalSymbols.StopBits1,
				globalSymbols.DataBits1, globalSymbols.Parity1);
		PORT2 = new comDevice(globalSymbols.Port2Name, globalSymbols.Baudrate2, globalSymbols.StopBits2,
				globalSymbols.DataBits2, globalSymbols.Parity2);

		if (PORT1 != null && PORT2 != null) {
			return true;
		} else {
			return false;
		}
	}

	public void checkAndSend(String data) {
		if (data.toLowerCase().contains("#") && data.toLowerCase().contains("$")) {
			PORT2.send(data);
			sendedDataCount += data.length();
		} else if (data.toLowerCase().contains("#")) {
			sendEnabled = true;
		} else if (data.toLowerCase().contains("$")) {
			PORT1.send(data);
			sendedDataCount += data.length();
			sendEnabled = false;
		}
		if (sendEnabled) {
			PORT1.send(data);
			sendedDataCount += data.length();
		}
		if (sendedDataCount > 20) {
			sendedDataCount = 0;
			sendEnabled = false;
		}
	}

}
