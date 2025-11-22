package module2a1;

public class JDisplay2 implements HighLevelDisplay {

    private JDisplay d;
    private String [] text;
    private  int usedRows;

    public JDisplay2(){
		d = new JDisplay();
		text = new String [100];
		clear();
    }

    private void updateRow(int row, String str) {
		text[row] = str;
		if (row < d.getRows()) {
			// 写入字符串内容
			for (int i = 0; i < str.length() && i < d.getCols(); i++) {
				d.write(row, i, str.charAt(i));
			}
			// 填充剩余部分为空格
			for (int i = str.length(); i < d.getCols(); i++) {
				d.write(row, i, ' ');
			}
		}
    }

    private void flashRow(int row, int millisecs) {
	String txt = text[row];
	try {
		for (int i = 0; i * 200 < millisecs; i++) {

			// 清空该行（闪烁 OFF）
			updateRow(row, "");
			Thread.sleep(70);

			// 恢复文字（闪烁 ON）
			updateRow(row, txt);
			Thread.sleep(130);
		}
	} catch (Exception e) {
	    System.err.println(e.getMessage());
	}
	
    }

    public void clear() {
		// 清空所有可见行
		for (int i = 0; i < d.getRows(); i++) {
			updateRow(i, "");
		}

		// 重置已使用的行数
		usedRows = 0;
    }

    public void addRow(String str) {
		updateRow(usedRows,str);
		flashRow(usedRows,1000);
		usedRows++;
    }

	public void deleteRow(int row) {
		if (row < usedRows) {

			// 将 row+1 到 usedRows-1 的行上移一行
			for (int i = row + 1; i < usedRows; i++) {
				updateRow(i - 1, text[i]);
			}

			// 更新已使用行数
			usedRows--;

			// 清空最后一行（因为它已被上移覆盖）
			updateRow(usedRows, "");

			// 若仍有内容超出屏幕范围，让屏幕底行闪烁
			if (usedRows >= d.getRows()) {
				flashRow(d.getRows() - 1, 1000);
			}
		}
	}


}