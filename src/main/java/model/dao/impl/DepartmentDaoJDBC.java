package model.dao.impl;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoJDBC implements DepartmentDao {

    private Connection conn;

    public DepartmentDaoJDBC(Connection conn){
        this.conn = conn;
    }

    @Override
    public void insert(Department department) {
        PreparedStatement st = null;
        try{
            st = conn.prepareStatement(
                    "insert into department "
                            + "(Name) "
                            + "VALUES "
                            + "(?)",
                    PreparedStatement.RETURN_GENERATED_KEYS
            );

            st.setString(1, department.getName());

            int rowsAffected = st.executeUpdate();
            if(rowsAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1);
                    department.setId(id);
                }
                DB.closeResultSet(rs);
            }
        }catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public void update(Department department) {
        PreparedStatement st = null;
        try{
            st = conn.prepareStatement(
                    "update department "
                            + "set Name = ? where Id = ?"
            );
            st.setString(1, department.getName());
            st.setInt(2, department.getId());

            st.executeUpdate();

        }catch (SQLException e) {
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try{
            st = conn.prepareStatement("DELETE FROM department WHERE Id = ?");
            st.setInt(1, id);
            st.executeUpdate();
        }catch (SQLException e){
            throw new DbException(e.getMessage());
        }finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = conn.prepareStatement(
                    "SELECT * from department WHERE Id = ?"
            );
            st.setInt(1, id);
            rs = st.executeQuery();
            if(rs.next())
                return instantiateDepartment(rs);
            return null;
        }catch(SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    @Override
    public List<Department> findAll() {
        PreparedStatement st = null;
        ResultSet rs = null;
        try{
            st = conn.prepareStatement(
                    "SELECT * from Department ORDER BY Name"
            );
            rs = st.executeQuery();
            List<Department> departaments = new ArrayList<>();
            while(rs.next())
                departaments.add(instantiateDepartment(rs));
            return departaments;
        }catch(SQLException e) {
            throw new DbException(e.getMessage());
        }
    }

    private Department instantiateDepartment(ResultSet rs) throws SQLException {
        Department dep = new Department();
        dep.setId(rs.getInt("Id"));
        dep.setName(rs.getString("Name"));
        return dep;
    }
}
