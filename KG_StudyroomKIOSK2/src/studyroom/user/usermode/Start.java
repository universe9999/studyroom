package studyroom.user.usermode;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JFrame;

import studyroom.MainPage;
import studyroom.design.Style;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Start extends JPanel {

	public static Connection conn;
	DateTimeFormatter date = DateTimeFormatter.ofPattern("yyyy�� M�� d��");
	DateTimeFormatter time = DateTimeFormatter.ofPattern("a h�� m�� ");
	static Timestamp time_chk_seat;
	static ArrayList<String> pass_price = new ArrayList<>();

	public Start() {

		pass_price = new ArrayList<>();
		new Style(this);
		this.setLayout(null);

		// �޴� ��ư 4��
		JButton seat_btn = new JButton("�¼� �̿��");
		seat_btn.setBounds(MainPage.w/2-230, MainPage.h/2-160, 220, 130);
		this.add(seat_btn);

		JButton locker_btn = new JButton("�繰�� �̿��");
		locker_btn.setBounds(MainPage.w/2-230, MainPage.h/2-10, 220, 130);
		this.add(locker_btn);

		JButton room_btn = new JButton("�� �̿��");
		room_btn.setBounds(MainPage.w/2+10, MainPage.h/2-160, 220, 130);
		this.add(room_btn);

		JButton back_btn = new JButton("���� ȭ��");
		back_btn.setBounds(MainPage.w/2+10, MainPage.h/2-10, 220, 130);
		this.add(back_btn);

		new Style(seat_btn);
		new Style(locker_btn);
		new Style(room_btn);
		new Style(back_btn);

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/XEPDB1", "hr", "1234");
			// �̿�� ���� ���̺� �ҷ�����
			String sql = "select * from seat_price_info,locker_price_info";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			int row=0;
			int loc=0;
			while (rs.next()) {
				if(loc==0) {
					pass_price.add(rs.getString(3));
					pass_price.add(Integer.toString(rs.getInt(4)));
					loc=1;
				}
				pass_price.add(rs.getString(1));
				pass_price.add(Integer.toString(rs.getInt(2)));
			} 

			seat_btn.addActionListener(new ActionListener() { //�¼� �̿�� ������
				@Override
				public void actionPerformed(ActionEvent e) {

					if(Mainmenu.seat_chk>0) {
						String msg= "������ �¼��� �̹� �����մϴ�";
						JOptionPane.showMessageDialog(null,msg); 
					} else if(Mainmenu.type.equals("���� �̿��")) {
						String msg= "���� �̿�� �̿��ڴ� �Խ��� �̿��ϼ���";
						JOptionPane.showMessageDialog(null,msg); 
					}else {
						MainPage.user_page_panel.add("�¼��̿��", new DayOrWeek()); // �¼� �̿�� ������
						MainPage.main_cards.show(MainPage.main_page_panel, "����ڸ޴�");
						MainPage.user_cards.show(MainPage.user_page_panel, "�¼��̿��");
						MainPage.userToggle = "�¼��̿��";
					}
				}
			}); 

			room_btn.addActionListener(new ActionListener() { //�� �̿�� ������
				@Override
				public void actionPerformed(ActionEvent e) {
					try {//�� ����ð��� �������� ���� �Ұ� 
						String sql = "SELECT room_number from person_info where login_state='On'";
						PreparedStatement pstmt = conn.prepareStatement(sql);
						ResultSet rs = pstmt.executeQuery();

						while(rs.next()) { 

							int room_chk = rs.getInt("room_number");
							if(room_chk>0) {
								String msg= "������ ���� �̹� �����մϴ�";
								JOptionPane.showMessageDialog(null,msg); 
							}else {                     
								MainPage.user_page_panel.add("���̿��", new DayRoom());// �� �̿�� ������
								MainPage.main_cards.show(MainPage.main_page_panel, "����ڸ޴�");
								MainPage.user_cards.show(MainPage.user_page_panel, "���̿��");
								MainPage.userToggle = "���̿��";
							}
						} 
					} catch (SQLException e1) { 
						e1.printStackTrace();
					} 
				}
			});  

			locker_btn.addActionListener(new ActionListener() { //�繰�� �̿�� ������
				@Override
				public void actionPerformed(ActionEvent e) {
					try {//�繰�Ը���ð��� �������� ���� �Ұ� 
						String sql = "SELECT locker_number from person_info where login_state='On'";
						PreparedStatement pstmt = conn.prepareStatement(sql);
						ResultSet rs = pstmt.executeQuery();

						while(rs.next()) { 

							int locker_chk = rs.getInt("locker_number");
							if(locker_chk>0) {
								String msg= "������ �繰���� �̹� �����մϴ�";
								JOptionPane.showMessageDialog(null,msg); 
							}else {    
								MainPage.user_page_panel.add("�繰���̿��", new Locker());
								MainPage.main_cards.show(MainPage.main_page_panel, "����ڸ޴�");
								MainPage.user_cards.show(MainPage.user_page_panel, "�繰���̿��");
								MainPage.userToggle = "�繰���̿��";
							}
						}

					} catch (SQLException e1) { 
						e1.printStackTrace();
					}
				}
			});

			back_btn.addActionListener(new ActionListener() { //���� ������
				@Override
				public void actionPerformed(ActionEvent e) {
					MainPage.user_page_panel.add("���θ޴�", new Mainmenu()); // �޴�������
					MainPage.main_cards.show(MainPage.main_page_panel, "����ڸ޴�");
					MainPage.user_cards.show(MainPage.user_page_panel, "���θ޴�");
					MainPage.userToggle = "���θ޴�";
				}
			});

		} catch (ClassNotFoundException | SQLException e1) {
			e1.printStackTrace();
		}
	}
}