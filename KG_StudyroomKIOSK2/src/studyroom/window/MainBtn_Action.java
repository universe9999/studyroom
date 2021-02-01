package studyroom.window;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.Format;
import java.util.Calendar;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import studyroom.MainPage;
import studyroom.admin.AdminPage;
import studyroom.design.Conversion_image;
import studyroom.design.Style;
import studyroom.swingTools.Login_SwingTool;
import studyroom.swingTools.SwingToolsSubPage;
import studyroom.user.findPW.FindPasswordPageUser;
import studyroom.user.login.LoginPage;
import studyroom.user.login.loginDataBase.DBLoggedIn;
import studyroom.user.signUp.SignUpPage;
import studyroom.user.usermode.Mainmenu;

public class MainBtn_Action implements ActionListener {
	public static int interval;
	JButton loginbtns;
	String nextcard = "";

	public MainBtn_Action(JButton loginbtns) {
		this.loginbtns = loginbtns;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String label1 = null;
		String label2 = null;

		if (loginbtns.getText().equals("ȸ������")) {
			MainPage.main_cards.show(MainPage.main_page_panel, "ȸ������");
			MainPage.userToggle = "ȸ������";
			SignUpPage.passAlert.setText(""); // ȸ������ ��� �˸� �ʱ�ȭ
			SignUpPage.passConfirmAlert.setText(""); // ȸ������ ���Ȯ�� �˸� �ʱ�ȭ

		} else if (loginbtns.getText().equals("���ã��")) {
			MainPage.main_cards.show(MainPage.main_page_panel, "���ã��");
			MainPage.userToggle = "���ã��";

		} else if (loginbtns.getText().equals("������ ���ã��")) {
			MainPage.main_cards.show(MainPage.main_page_panel, "���ã��");
			MainPage.userToggle = "������ ���ã��";

		} else if (loginbtns.getText().equals("�����ڹ�ư")) {
			// ������ ��ư
			if (MainPage.userToggle.equals("����") || MainPage.userToggle.equals("�α���")) {
				MainPage.main_cards.show(MainPage.main_page_panel, "������");
				MainPage.userToggle = "������";
			} else {
				// ùȭ��
				MainPage.main_cards.show(MainPage.main_page_panel, "����");
				MainPage.userToggle = "����";
			}

		} else if (loginbtns.getText().equals("�α׾ƿ�")) {

			// �α׾ƿ�
			try {
				interval = -1;
				Class.forName("oracle.jdbc.driver.OracleDriver");
				Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/XEPDB1", "hr", "1234");
				MainPage.main_cards.show(MainPage.main_page_panel, "�α���");
				MainPage.userToggle = "�α���";

				PreparedStatement pstmt = null;
				String sql = "update person_info set login_state ='Off'";
				pstmt = conn.prepareStatement(sql);

				int row = pstmt.executeUpdate();

				PreparedStatement pstmt2 = null;
				String sql2 = "update Admin_Info set Admin_LoginState = 'Off'";
				pstmt2 = conn.prepareStatement(sql2);

				int row2 = pstmt2.executeUpdate();

				if (pstmt2 != null)
					pstmt.close();
				if (pstmt != null)
					pstmt.close();
				if (conn != null)
					conn.close();

				// ���� Ƚ�� ���� ��Ȱ��ȭ
				MainPage.extend_cnt = 3;
				MainPage.extendbtn.setEnabled(true);

				// ī�巹�̾ƿ� ǥ�� �Ǵ� ������
				MainPage.logoutcard.show(MainPage.logout, "1");
				MainPage.extendcard.show(MainPage.extend, "1");
				MainPage.homecard.show(MainPage.home, "1");
				MainPage.changeUsercard.show(MainPage.changeUser, "1");

			} catch (ClassNotFoundException | SQLException e1) {
				e1.printStackTrace();
			}

		} else if (loginbtns.getText().contains("Ȩ")) {// �߰��κ�

			MainPage.user_page_panel.add("���θ޴�", new Mainmenu()); // �޴�������
			MainPage.main_cards.show(MainPage.main_page_panel, "����ڸ޴�");
			MainPage.user_cards.show(MainPage.user_page_panel, "���θ޴�");
			MainPage.userToggle = "���θ޴�";

		} else if (loginbtns.getText().contains("��ġ")) {
			MainPage.main_cards.show(MainPage.main_page_panel, "�α���");
			MainPage.userToggle = "�α���";

		} else if (loginbtns.getText().equals("������ �α���")) {

			String admin_phonenumber = AdminPage.admin_phone_number1.getText() + "-"
					+ AdminPage.admin_phone_number2.getText() + "-" + AdminPage.admin_phone_number3.getText();
			String admin_password = String.valueOf(AdminPage.admin_loginpass.getPassword());

			// ������ �α��� Ŭ�� ��
			new DBLoggedIn(admin_phonenumber, admin_password);

			if (DBLoggedIn.person_name != null && DBLoggedIn.phone_number.equals(admin_phonenumber)
					&& DBLoggedIn.password.equals(admin_password)) {
				// ��ȣ�� ����� ��ġ �ϸ�
				label1 = "ȸ����ȣ : " + DBLoggedIn.person_id;
				label2 = DBLoggedIn.person_name + "�� ȯ���մϴ� !!";

				String update = "update admin_info set admin_loginstate = 'On' " + "where admin_phonenumber = '"
						+ DBLoggedIn.phone_number + "' and admin_pw = '" + DBLoggedIn.password + "'";
				DBLoggedIn db = new DBLoggedIn(update);

				interval = 300;// �ڵ� �α׾ƿ� ī��Ʈ �ð�

				// ��ư �̸�
				nextcard = "�����ڸ޴�";
				new SubWindow(label1, label2, nextcard);

			} else {
				label1 = "�������� ���� ���̵�ų�";
				label2 = "�߸��� ��й�ȣ�Դϴ�.";
				new SubWindow(label1, label2);
			}

		} else if (loginbtns.getText().equals("�α���")) {

			MainPage.userToggle = "�α���";

			String login_phonenumber = LoginPage.phone_number1.getText() + "-" + LoginPage.phone_number2.getText() + "-"
					+ LoginPage.phone_number3.getText();
			String login_password = String.valueOf(LoginPage.loginpass.getPassword());
			// �α��� Ŭ�� ��
			// �ݱ� �ϸ� �������� �ѱ��?

			new DBLoggedIn(login_phonenumber, login_password);

			if (DBLoggedIn.person_name != null && DBLoggedIn.phone_number.equals(login_phonenumber)
					&& DBLoggedIn.password.equals(login_password)) {

				// ��ȣ�� ����� ��ġ �ϸ�
				label1 = "ȸ����ȣ : " + DBLoggedIn.person_id;
				label2 = DBLoggedIn.person_name + "�� ȯ���մϴ� !!";

				String update = "update person_info set login_state = 'On' " + "where phone_number = '"
						+ DBLoggedIn.phone_number + "' and pw = '" + DBLoggedIn.password + "'";
				DBLoggedIn db = new DBLoggedIn(update);

				interval = 300;// �ڵ� �α׾ƿ� ī��Ʈ �ð�

				// ��ư �̸�
				nextcard = "����ڸ޴�";
				new SubWindow(label1, label2, nextcard);

			} else {
				label1 = "�������� ���� ���̵�ų�";
				label2 = "�߸��� ��й�ȣ�Դϴ�.";
				new SubWindow(label1, label2);
			}

		} else {

			// ������ �غ���

			label1 = "[system] still in maintenance";
			label2 = "������ �غ� ��..";
			new SubWindow(label1, label2);
		}

		// ��й�ȣ ã�� �ʱ�ȭ
		FindPasswordPageUser.phone_number1.setText("010");
		FindPasswordPageUser.phone_number1.setText("010");
		FindPasswordPageUser.phone_number1.setText("010");
		FindPasswordPageUser.year.setSelectedItem("2000");

		// �α��� �� �ʱ�ȭ
		LoginPage.phone_number1.setText("010");
		LoginPage.phone_number2.setText("");
		LoginPage.phone_number3.setText("");
		LoginPage.loginpass.setText("");
		AdminPage.admin_phone_number1.setText("010");
		AdminPage.admin_phone_number2.setText("");
		AdminPage.admin_phone_number3.setText("");
		AdminPage.admin_loginpass.setText("");
	}

}