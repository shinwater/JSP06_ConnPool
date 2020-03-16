package com.sist.model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class BoardDAO {

	Connection con = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	String sql = null;

	// 싱글톤 객체 만들기
	// 1. 싱글톤 객체를 만들때는 우선적으로 접근지정자는 private으로 선언한다.
	// 2. 정적 멤버로 선언한다. -static으로 선언

	private static BoardDAO instance = new BoardDAO();// static:메서드 영역에 만들어진다아

	// 3. 기본생성자는 외부에서 접근이 되지 않도록 해야한다. -private으로 생성자 생성
	// 외부에서 new를 사용하지 못하게 하는 접근 기법.
	private BoardDAO() {
	}

	// 4. 생성자 대신에 싱글톤 객체를 return 해주는 getInstance() 메서드를 만들어 주자.
	public static BoardDAO getInstance() {// static에 있는 instance를 받아줘야하기때문에 static
		if (instance == null) {// 객체생성했기때문에 null일리가 없지만 혹시나모르니까아...
			instance = new BoardDAO();
		}
		return instance; // 참조변수 리턴!
	}

	public void openConn() {

		try {
			// 1. JNDI 서버 객체 생성
			InitialContext ic = new InitialContext();

			// 2.lookup() 메서드를 이용하여 매칭되는 커넥션을 찾는다.

			DataSource ds = (DataSource) ic.lookup("java:comp/env/jdbc/myoracle");

			// 3.DataSource 객체를 이용하여 커넥션 객체를 하나 가져온다.
			con = ds.getConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}// openConn() end

	// board1 테이블의 전체 레코드를 조회하여 ArrayList 객체에 저장
	public List<BoardDTO> getList() {
		List<BoardDTO> board = new ArrayList<BoardDTO>();

		try {

			openConn();
			sql = "select * from board1 order by board_no desc"; // 최신글이 앞으로 나와야하니까 ~
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();

			while (rs.next()) { // next() : 데이터가 존재하면 true값 반환, 없으면 false값 반환
				// 테이블에서 하나의 레코드를 가져와서 각각의 컬럼을 DTO객체에 저장.
				BoardDTO dto = new BoardDTO();
				dto.setBoard_no(rs.getInt("board_no"));
				dto.setBoard_writer(rs.getString("board_writer"));
				dto.setBoard_title(rs.getString("board_title"));
				dto.setBoard_cont(rs.getString("board_cont"));
				dto.setBoard_pwd(rs.getString("board_pwd"));
				dto.setBoard_hit(rs.getInt("board_hit"));
				dto.setBoard_regdate(rs.getString("board_regdate"));

				board.add(dto);// dto:주소값 저장~~~~
			}
			// open 객체 닫기
			rs.close();
			pstmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return board;

	}// getList() 메서드 end

	// board1 테이블의 글번호에 해당하는 조회슈를 증가시ㅣ는 메서드~~~
	public void boardHit(int no) {

		try {
			openConn();
			sql = "update board1 set board_hit = board_hit + 1 where board_no=?";

			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, no);

			pstmt.executeUpdate();

			// open객체 닫기
			pstmt.close(); // con.close(); : 이거는 닫아주면 안됑.. 왜? 상세네역가져와야하니까
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}// board Hit() 메서드 종료

	// 글번호에 해당하는 글을 조회하는 메서드
	public BoardDTO getCont(int no) {// BoardDTO 가 반환타입인거 중요~~~
		BoardDTO dto = new BoardDTO();

		try {
			openConn();
			sql = "select * from board1 where board_no=?";
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, no);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto.setBoard_no(rs.getInt("board_no"));
				dto.setBoard_writer(rs.getString("board_writer"));
				dto.setBoard_title(rs.getString("board_title"));
				dto.setBoard_cont(rs.getString("board_cont"));
				dto.setBoard_pwd(rs.getString("board_pwd"));
				dto.setBoard_hit(rs.getInt("board_hit"));
				dto.setBoard_regdate(rs.getString("board_regdate"));
			}
			// open 객체 ㅏㄷ닫기
			rs.close();
			pstmt.close();
			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dto;
	}// getCont() 메서드 end

	// board1 테이블에 게시글을 추가하는 메서드
	public int insertBoard(BoardDTO dto) {// 인자값 자료형이 일치해야함!! BoardDTO dto 중요오..
		int result = 0; // 정상적으로 레코드가 반영이되면 "1", 아니면 1이아닌 다른값 반환!

		try {
			openConn();
			sql = "insert into board1 values(board1_seq.nextval,?,?,?,?,default,sysdate)";
			pstmt = con.prepareStatement(sql);

			pstmt.setString(1, dto.getBoard_writer());
			pstmt.setString(2, dto.getBoard_title());
			pstmt.setString(3, dto.getBoard_cont());
			pstmt.setString(4, dto.getBoard_pwd());
			result = pstmt.executeUpdate();// 결과값은 int형으로 반환되니깐 result에 저~장~

			// open객체 닫기
			pstmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;

	}

	// board1 테이블에 게시글 수정하는 메서드

	public int updateBoard(BoardDTO dto) {
		int result = 0;

		try {
			openConn();
			sql = "select * from board1 where board_no=?";

			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, dto.getBoard_no());
			rs = pstmt.executeQuery();

			if (rs.next()) {
				// (수정폼창에서 입력한 비밀번호= 내가 처음에 입력한 비밀번호) 이면 수정해라!
				if (dto.getBoard_pwd().equals(rs.getString("board_pwd"))) {
					sql = "update board1 set board_title=?," + "board_cont=? where board_no=?";

					pstmt = con.prepareStatement(sql);
					pstmt.setString(1, dto.getBoard_title());
					pstmt.setString(2, dto.getBoard_cont());
					pstmt.setInt(3, dto.getBoard_no());
					result = pstmt.executeUpdate(); // 성공하면 숫자1반환.
				} else { // 비밀번호가 틀린 경우
					result = -1;
				}
			}

			// open객체 닫기
			rs.close();
			pstmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}// updateBoard() 닫기

	// board1 테이블에서 게시글을 삭제하는 메서드
	public int deleteBoard(int no, String pwd) {
		int result = 0;

		try {
			openConn();
			sql = "select * from board1 where board_no=?";

			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, no);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				// (삭제폼창에서 입력한 비밀번호= 내가 처음에 입력한 비밀번호) 이면 수정해라!
				if (pwd.equals(rs.getString("board_pwd"))) {
					sql = "delete from board1 where board_no=?";

					pstmt = con.prepareStatement(sql);
					pstmt.setInt(1, no);
					result = pstmt.executeUpdate(); // 성공하면 숫자1반환.
				} else { // 비밀번호가 틀린 경
					result = -1;
				}
			}

			// open객체 닫기
			rs.close();
			pstmt.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public List<BoardDTO> searchBoard(String find_field, String find_name) {
		List<BoardDTO> search = new ArrayList<BoardDTO>();

		if (find_field.equals("title")) { // 제목으로 검색한 경우

			try {
				sql = "select * from board1 where board_title like ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, "%" + find_name + "%");
				rs = pstmt.executeQuery();

				while (rs.next()) { // next() : 데이터가 존재하면 true값 반환, 없으면 false값 반환
					// 테이블에서 하나의 레코드를 가져와서 각각의 컬럼을 DTO객체에 저장.
					BoardDTO dto = new BoardDTO();
					dto.setBoard_no(rs.getInt("board_no"));
					dto.setBoard_writer(rs.getString("board_writer"));
					dto.setBoard_title(rs.getString("board_title"));
					dto.setBoard_cont(rs.getString("board_cont"));
					dto.setBoard_pwd(rs.getString("board_pwd"));
					dto.setBoard_hit(rs.getInt("board_hit"));
					dto.setBoard_regdate(rs.getString("board_regdate"));

					search.add(dto);// dto:주소값 저장~~~~
				}
				// open 객체 닫기
				rs.close();
				pstmt.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (find_field.equals("cont")) {
			try {
				sql = "select * from board1 where board_cont like ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, "%" + find_name + "%");
				rs = pstmt.executeQuery();

				while (rs.next()) { // next() : 데이터가 존재하면 true값 반환, 없으면 false값 반환
					// 테이블에서 하나의 레코드를 가져와서 각각의 컬럼을 DTO객체에 저장.
					BoardDTO dto = new BoardDTO();
					dto.setBoard_no(rs.getInt("board_no"));
					dto.setBoard_writer(rs.getString("board_writer"));
					dto.setBoard_title(rs.getString("board_title"));
					dto.setBoard_cont(rs.getString("board_cont"));
					dto.setBoard_pwd(rs.getString("board_pwd"));
					dto.setBoard_hit(rs.getInt("board_hit"));
					dto.setBoard_regdate(rs.getString("board_regdate"));

					search.add(dto);// dto:주소값 저장~~~~
				}
				// open 객체 닫기
				rs.close();
				pstmt.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else { // title_cont
			try {
				sql = "select * from board1 where board_title like ?" + "or board_cont like ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setString(1, "%" + find_name + "%");
				pstmt.setString(2, "%" + find_name + "%");
				rs = pstmt.executeQuery();

				while (rs.next()) { // next() : 데이터가 존재하면 true값 반환, 없으면 false값 반환
					// 테이블에서 하나의 레코드를 가져와서 각각의 컬럼을 DTO객체에 저장.
					BoardDTO dto = new BoardDTO();
					dto.setBoard_no(rs.getInt("board_no"));
					dto.setBoard_writer(rs.getString("board_writer"));
					dto.setBoard_title(rs.getString("board_title"));
					dto.setBoard_cont(rs.getString("board_cont"));
					dto.setBoard_pwd(rs.getString("board_pwd"));
					dto.setBoard_hit(rs.getInt("board_hit"));
					dto.setBoard_regdate(rs.getString("board_regdate"));

					search.add(dto);// dto:주소값 저장~~~~
				}
				// open 객체 닫기
				rs.close();
				pstmt.close();
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return search;
	}// searchBoard() 메서드 end
}
