package com.github.xzzpig.pigutils.core;

import java.io.File;
import java.io.IOException;

public interface DataContainer<T extends IData> {
	T getData();

	File getDataFile();

	void loadData();

	void saveData() throws IOException;
}
