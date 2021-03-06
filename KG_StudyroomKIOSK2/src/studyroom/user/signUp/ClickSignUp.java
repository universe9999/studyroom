package studyroom.user.signUp;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import studyroom.user.signUp.window.ResultWindow;
import studyroom.user.usermode.Time;

public class ClickSignUp extends MouseAdapter {

	static int person_id;
	static String person_name;

	static String phoneNumber;

	// 회원가입 페이지에서 가입 버튼 눌렀을 시
	@Override
	public void mouseClicked(MouseEvent e) {
		boolean consentCheck = true;
		JTextField pw;
		JTextField pwConfirm;
		String year = (String) SignUpPage.year.getSelectedItem();
		String month = (String) SignUpPage.month.getSelectedItem();
		String day = (String) SignUpPage.day.getSelectedItem();
		boolean samePhoneNumber = false;
		JTextField phoneNumber1 = SignUpPage.phone_number1;
		JTextField phoneNumber2 = SignUpPage.phone_number2;
		JTextField phoneNumber3 = SignUpPage.phone_number3;
		String text = phoneNumber1.getText() + "-" + phoneNumber2.getText() + "-" + phoneNumber3.getText();

		pw = SignUpEnum.PASSWORD.blindPW;
		pwConfirm = SignUpEnum.PASSWORDCONFIRM.blindPW;

		// 가입 조건 필터링
		if (e.getButton() == MouseEvent.BUTTON1) {
			if (!Pattern.matches("[가-힣]{2,4}", SignUpEnum.NAME.text.getText())) {
				new ResultWindow("성 함");
				SignUpEnum.NAME.text.setText("");
				;
			} else if (!Pattern.matches("[0-9]{8}", year + month + day)) {
				new ResultWindow("생년 월일");
			} else if (!(Pattern.matches("01[0-9]", phoneNumber1.getText())
					&& Pattern.matches("[0-9]{4}", phoneNumber2.getText())
					&& Pattern.matches("[0-9]{4}", phoneNumber3.getText()))) {
				new ResultWindow("전화 번호");
				phoneNumber1.setText("");
				phoneNumber2.setText("");
				phoneNumber3.setText("");
			} else if (!Pattern.matches("^(?=.*[0-9])(?=.*[A-Z])(?=.*[a-z]).{8,12}$", pw.getText())) {
				new ResultWindow("비밀 번호");
				pw.setText("");
				pwConfirm.setText("");
			} else if (!pw.getText().equals(pwConfirm.getText())
					|| pw.getText().equals(SignUpEnum.PASSWORDCONFIRM.labelName)) {
				new ResultWindow("비밀 번호 확인");
				pwConfirm.setText("");
			} else {
				for (Entry<JCheckBox, JButton> kv : SignUpPage.consent.entrySet()) {
					if (!kv.getKey().isSelected()) {
						consentCheck = false;
						new ResultWindow("약관 동의");
						break;
					}
				}
				// 필터링 거치고 걸러진게 없다면, 아래 가입DB 실행
				if (consentCheck) {
					
					try {

						Class.forName("oracle.jdbc.driver.OracleDriver");

						Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521/XEPDB1", "hr",
								"1234");

						conn.setAutoCommit(false);
						//
						PreparedStatement read_PhoneNumber = conn
								.prepareStatement("SELECT phone_number FROM person_info");

						ResultSet rs = read_PhoneNumber.executeQuery();

						// 전화 번호 중복이라면 필터링
						while (rs.next()) {
							phoneNumber = rs.getString(1);
							if (text.equals(phoneNumber)) {
								new ResultWindow("전화 번호 중복");
								phoneNumber1.setText("");
								phoneNumber2.setText("");
								phoneNumber3.setText("");
								samePhoneNumber = true;
								break;
							}
						}

						if (rs != null)
							rs.close();
						if (read_PhoneNumber != null)
							read_PhoneNumber.close();

						// 전화번호도 중복이 아니라면
						if (!samePhoneNumber) {

							PreparedStatement read_MaxIDNum = conn
									.prepareStatement("select max(person_id) from person_info group by person_id");

							ResultSet rs1 = read_MaxIDNum.executeQuery();

							while (rs1.next()) {
								person_id = rs1.getInt(1);
							}

							PreparedStatement insertPersonInfo = conn.prepareStatement("INSERT INTO Person_Info "
									+ "(Person_Id,Check_Time,Person_Name, person_birth, Phone_Number,PW,Total_Payment)"
									+ " VALUES(?, ?, ?, ?, ?, ?, ?)");

							DateFormat simple = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
							Date now = new Date();

							insertPersonInfo.setInt(1, person_id + 1);
							insertPersonInfo.setString(2, simple.format(now));
							insertPersonInfo.setString(3, SignUpEnum.NAME.text.getText());
							insertPersonInfo.setString(4, year + month + day);
							insertPersonInfo.setString(5, text);
							insertPersonInfo.setString(6, pw.getText());
							insertPersonInfo.setInt(7, 0);

							insertPersonInfo.addBatch();

							int[] rows = insertPersonInfo.executeBatch();

							if (insertPersonInfo != null)
								insertPersonInfo.close();

							PreparedStatement read_name_ID_from_personInfo = conn.prepareStatement(
									"SELECT person_id, person_name FROM person_info where phone_number = ?");

							read_name_ID_from_personInfo.setString(1, text);

							ResultSet rs2 = read_name_ID_from_personInfo.executeQuery();

							while (rs2.next()) {
								person_id = rs2.getInt(1);
								person_name = rs2.getString(2);
							}

							// 회원번호, 성함을 가입성공 페이지로 보내기
							new ResultWindow(person_id, person_name);

							if (rs1 != null)
								rs1.close();
							if (read_MaxIDNum != null)
								read_MaxIDNum.close();
							if (rs2 != null)
								rs2.close();
							if (read_name_ID_from_personInfo != null)
								read_name_ID_from_personInfo.close();
							if (conn != null)
								conn.close();

							//System.out.println("성공");
						}

					} catch (SQLException e1) {
						 e1.printStackTrace();
						//System.out.println(e1.toString());

					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
						//System.out.println("[ojdbc] 클래스 경로가 틀렸습니다.");
					}
				}
			}
		}
	}

}
