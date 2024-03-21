package com.book.store.system.Entities;

import java.sql.Connection;

public interface DBObj {

    public boolean init(Connection conn);
    
}