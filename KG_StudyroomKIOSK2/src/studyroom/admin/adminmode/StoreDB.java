package studyroom.admin.adminmode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import studyroom.design.Style;
import studyroom.user.usermode.Time;

public class StoreDB {

	JTable table;
	DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a hh시 mm분 ss초");
	Timestamp time;
	String[][] contents;
	int num = 0;
	int row = 0;
	int length;

	public StoreDB(String type) {

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/XEPDB1", "hr", "1234");
			String sql = "";

			if (type == "좌석") {

				sql = "SELECT seat_number, seat_statement, time_enter,time_checkout FROM seat WHERE seat_statement = '사용 중' AND seat_number < 100";
				String header[] = { "좌석", "사용 여부", "입실 시간", "퇴실(/예정)시간" };
				length = header.length;
				PreparedStatement pstm = conn.prepareStatement(sql);
				ResultSet rs = pstm.executeQuery();
				while (rs.next()) {
					num++;
				}
				contents = new String[num][length];
				ResultSet rs2 = pstm.executeQuery();
				while (rs2.next()) {
					contents[row][0] = Integer.toString(rs2.getInt(1));
					contents[row][1] = rs2.getString(2);
					time = rs2.getTimestamp(3);
					contents[row][2] = time.toString().substring(0, 19);
					time = rs2.getTimestamp(4);
					contents[row][3] = time.toString().substring(0, 19);
					row++;
				}
				DefaultTableModel model = new DefaultTableModel(contents, header);
				table = new JTable(model);
				if (rs != null)
					rs.close();
				if (rs2 != null)
					rs2.close();
				if (pstm != null)
					pstm.close();

			} else if (type == "룸") {

				sql = "SELECT seat_number, seat_statement, time_enter,time_checkout FROM seat WHERE seat_statement = '사용 중' AND seat_number>100";
				String header[] = { "룸", "사용 여부", "입실 시간", "퇴실(/예정)시간" };
				length = header.length;
				PreparedStatement pstm = conn.prepareStatement(sql);
				ResultSet rs3 = pstm.executeQuery();
				while (rs3.next()) {
					num++;
				}
				contents = new String[num][length];
				ResultSet rs4 = pstm.executeQuery();
				while (rs4.next()) {
					contents[row][0] = Integer.toString(rs4.getInt(1));
					contents[row][1] = rs4.getString(2);
					time = rs4.getTimestamp(3);
					contents[row][2] = time.toString().substring(0, 19);
					time = rs4.getTimestamp(4);
					contents[row][3] = time.toString().substring(0, 19);
					row++;
				}
				DefaultTableModel model = new DefaultTableModel(contents, header);
				table = new JTable(model);
				if (rs3 != null)
					rs3.close();
				if (rs4 != null)
					rs4.close();
				if (pstm != null)
					pstm.close();

			} else if (type == "사물함") {

				sql = "SELECT locker_number, locker_statement, l_time_enter,l_time_checkout FROM locker WHERE locker_statement = '사용 중'";
				String header[] = { "사물함", "사용 여부", "사용 시작 시간", "만료(/예정)시간" };
				length = header.length;
				PreparedStatement pstm = conn.prepareStatement(sql);
				ResultSet rs5 = pstm.executeQuery();
				while (rs5.next()) {
					num++;
				}
				contents = new String[num][length];
				ResultSet rs6 = pstm.executeQuery();
				while (rs6.next()) {
					contents[row][0] = Integer.toString(rs6.getInt(1));
					contents[row][1] = rs6.getString(2);
					time = rs6.getTimestamp(3);
					contents[row][2] = time.toString().substring(0, 19);
					time = rs6.getTimestamp(4);
					contents[row][3] = time.toString().substring(0, 19);
					row++;
				}
				DefaultTableModel model = new DefaultTableModel(contents, header);
				table = new JTable(model);
				if (rs5 != null)
					rs5.close();
				if (rs6 != null)
					rs6.close();
				if (pstm != null)
					pstm.close();
			}
			table.getColumnModel().getColumn(0).setPreferredWidth(50);
			table.getColumnModel().getColumn(1).setPreferredWidth(60);
			table.getColumnModel().getColumn(2).setPreferredWidth(190);
			table.getColumnModel().getColumn(3).setPreferredWidth(190);
			// table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
			table.setRowHeight(35);
			new Style(table);
			
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(JLabel.CENTER);
			centerRenderer.setOpaque(false);
			for (int i = 0; i < 4; i++) {
				table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
			}

			StoreDBPage.total.setText("사용 중인 " + type + " 수 : " + num);
			StoreDBPage.scrollPane.setViewportView(table);
			JTableHeader headers = table.getTableHeader();
			new Style(headers);

			if (conn != null)
				conn.close();

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}
}