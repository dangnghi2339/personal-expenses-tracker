package com.example.dack1.data.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.Ignore;
/**
 * Đây là lớp Entity đại diện cho bảng 'transactions' trong cơ sở dữ liệu.
 * Room sẽ sử dụng lớp này để tạo bảng và các đối tượng giao dịch.
 */
@Entity(tableName = "transactions")
public class Transaction {

    /**
     * Khóa chính của bảng.
     * @PrimaryKey đánh dấu đây là khóa chính.
     * autoGenerate = true để Room tự động gán giá trị tăng dần mỗi khi
     * một giao dịch mới được thêm vào, đảm bảo mỗi giao dịch là duy nhất.
     */
    @PrimaryKey(autoGenerate = true)
    public long id;

    /**
     * Số tiền của giao dịch.
     * @ColumnInfo(name = "...") để chỉ định tên cột tương ứng trong CSDL.
     * Nếu không có, Room sẽ dùng tên biến làm tên cột.
     */
    @ColumnInfo(name = "amount")
    public double amount;

    /**
     * Loại giao dịch, ví dụ: "INCOME" (thu nhập) hoặc "EXPENSE" (chi tiêu).
     * Dùng String giúp chúng ta linh hoạt hơn trong tương lai.
     */
    @ColumnInfo(name = "type")
    public String type;

    /**
     * Ngày giao dịch, được lưu dưới dạng Unix Timestamp (kiểu long).
     * Cách này giúp việc sắp xếp và truy vấn theo thời gian cực kỳ hiệu quả.
     */
    @ColumnInfo(name = "transaction_date")
    public long transactionDate;

    /**
     * Ghi chú hoặc mô tả cho giao dịch.
     */
    @ColumnInfo(name = "description")
    public String description;

    /**
     * Khóa ngoại, liên kết giao dịch này với một danh mục (Category).
     * Ví dụ: Giao dịch "Cà phê" sẽ có categoryId trỏ đến danh mục "Ăn uống".
     */
    @ColumnInfo(name = "category_id")
    public long categoryId;

    // Room yêu cầu một constructor rỗng để có thể tái tạo lại đối tượng.
    public Transaction() {
    }

    public Transaction(long id, double amount, String type, long transactionDate, String description, long categoryId) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.transactionDate = transactionDate;
        this.description = description;
        this.categoryId = categoryId;
    }
    @Ignore
    public Transaction(double amount, String type, long transactionDate, String description, long categoryId) {
        this.amount = amount;
        this.type = type;
        this.transactionDate = transactionDate;
        this.description = description;
        this.categoryId = categoryId;
    }
    // Bạn có thể tạo thêm các constructor khác để tiện cho việc tạo đối tượng mới.
    // Tạm thời chúng ta sẽ để trống và sẽ bổ sung sau nếu cần.


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(long transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }
}