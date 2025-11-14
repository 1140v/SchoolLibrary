import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;

// 简单的测试类来验证数据库结构
public class DatabaseTest {
    
    public static void testDatabase(Context context) {
        // 打开数据库
        SQLiteDatabase db = context.openOrCreateDatabase("schoolbook.db", Context.MODE_PRIVATE, null);
        
        // 检查用户表是否存在
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='users'", null);
        boolean tableExists = cursor.getCount() > 0;
        cursor.close();
        
        if (tableExists) {
            System.out.println("✓ 用户表创建成功");
            
            // 检查表结构
            cursor = db.rawQuery("PRAGMA table_info(users)", null);
            System.out.println("用户表结构:");
            while (cursor.moveToNext()) {
                String columnName = cursor.getString(cursor.getColumnIndex("name"));
                String columnType = cursor.getString(cursor.getColumnIndex("type"));
                System.out.println("  - " + columnName + ": " + columnType);
            }
            cursor.close();
            
            // 检查默认管理员账户
            cursor = db.rawQuery("SELECT * FROM users WHERE username = 'admin'", null);
            if (cursor.getCount() > 0) {
                System.out.println("✓ 默认管理员账户存在");
            } else {
                System.out.println("✗ 默认管理员账户不存在");
            }
            cursor.close();
            
        } else {
            System.out.println("✗ 用户表不存在");
        }
        
        db.close();
    }
}