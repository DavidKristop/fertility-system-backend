package com.group3.backend.utils;

public class Constants {
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final int REQUEST_APPOINTMENT_ESTIMATED_TIME = 30;
    public static final int DEADLINE_SIGN_DATE_IN_HOURS = 48;
    public static final int DEADLINE_PAYMENT_DEADLINE_IN_HOURS = 48;
    public static final String CONTRACT_DRUG_AND_SERVICES_TABLE ="""
            <tr class="phase-row">
                <td>
                    <span class="phase-name" id="phase-name">{phase_name}</span>
                    <table class="table-phase">
                        <thead>
                            <tr>
                                <th>Tên Dịch Vụ/ Thuốc</th>
                                <th>Số Lượng</th>
                                <th>Đơn vị</th>
                                <th>Đơn Giá</th>
                                <th>Tổng cộng</th>
                            </tr>
                        </thead>
                        <tbody>
                            <tr class="service-row">
                                <td id="service-drug-name">{service_name}</td>
                                <td id="service-drug-quantity">{quantity}</td>
                                <td id="service-drug-unit">{unit}</td>
                                <td id="service-drug-unit-price">{unit_price}</td>
                                <td id="service-drug-total-price">{total_price}</td>
                            </tr>
                        </tbody>
                    </table>
                </td>
                <td class="phase-total" id="phase-total">{phase_total}</td>
                <td class="phase-total" id="refund-amount">{refund_amount}</td>
            </tr>
    """;
    public static final String CONTRACT_TEMPLATE = """
        <!DOCTYPE html>
        <html lang="vi">
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Hợp Đồng Dịch Vụ Trị Liệu Vô Sinh</title>
            <style>
                body {
                    font-family: "Times New Roman", sans-serif;
                    max-width: 800px;
                    margin: 0 auto;
                    padding: 20px;
                    line-height: 1.6;
                }
                h1 {
                    text-align: center;
                    margin-bottom: 30px;
                    color: #333;
                }
                .introduction {
                    margin-bottom: 30px;
                    padding: 15px;
                    border-radius: 5px;
                }
                .section {
                    margin-bottom: 25px;
                }
                .section-header {
                    color: #2c3e50;
                    margin-bottom: 15px;
                    font-weight: bold;
                }
                table {
                    width: 100%;
                    border-collapse: collapse;
                    margin: 15px 0;
                }
                table.table-phase {
                    border: 1px solid #ddd;
                }
                .phase-row {
                    background-color: #f4f4f4;
                    font-weight: bold;
                }
                .phase-total {
                    text-align: right;
                    font-weight: bold;
                }
                .service-row {
                    background-color: #fff;
                }
                th, td {
                    border: 1px solid #ddd;
                    padding: 8px;
                    text-align: left;
                }
                th {
                    background-color: #f4f4f4;
                }
                .checkbox-list {
                    margin-left: 20px;
                }
                .checkbox-list input[type="checkbox"] {
                    margin-right: 5px;
                }
                .signature-section {
                    display: flex;
                    justify-content: space-between;
                    margin-top: 40px;
                }
                .signature-box {
                    width: 45%;
                    text-align: center;
                    padding: 20px;
                    border: 1px solid #ddd;
                    border-radius: 5px;
                }
                .date {
                    margin-top: 20px;
                    font-style: italic;
                }
            </style>
        </head>
        <body>
            <h1>HỢP ĐỒNG DỊCH VỤ TRỊ LIỆU VÔ SINH</h1>
            
            <div class="introduction">
                <p>Trung tâm UCARE Fertility xin chào mừng quý khách đã tin tưởng lựa chọn dịch vụ của chúng tôi. Chúng tôi cam kết mang đến cho quý khách những dịch vụ điều trị vô sinh chất lượng cao nhất với sự tận tâm và chuyên nghiệp.</p>
                <p>Hợp đồng này quy định rõ ràng các điều khoản về chi phí và thanh toán cho gói điều trị của quý khách. Chúng tôi mong rằng thông qua hợp đồng này, quý khách sẽ có cái nhìn rõ ràng về quá trình điều trị và các khoản thanh toán.</p>
            </div>

            <div class="section">
                <h2 class="section-header">1. DỊCH VỤ VÀ THUỐC</h2>
                <table id="drug-and-services">
                    <thead>
                        <tr>
                            <th>Giai đoạn</th>
                            <th>Tổng giai đoạn</th>
                            <th>Phần trăm hoàn tiền</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr class="phase-row">
                            <td>
                                <span class="phase-name" id="phase-name">{phase_name}</span>
                                <table class="table-phase">
                                    <thead>
                                        <tr>
                                            <th>Tên Dịch Vụ/ Thuốc</th>
                                            <th>Số Lượng</th>
                                            <th>Đơn vị</th>
                                            <th>Đơn Giá</th>
                                            <th>Tổng cộng</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr class="service-row">
                                            <td id="service-drug-name">{service_name}</td>
                                            <td id="service-drug-quantity">{quantity}</td>
                                            <td id="service-drug-unit">{unit}</td>
                                            <td id="service-drug-unit-price">{unit_price}</td>
                                            <td id="service-drug-total-price">{total_price}</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </td>
                            <td class="phase-total" id="phase-total">{phase_total}</td>
                            <td class="phase-total" id="refund-amount">{refund_amount}</td>
                        </tr>
                    </tbody>
                </table>
                <p id="total">Tổng cộng: {total}</p>
            </div>

            <div class="section">
                <h2 class="section-header">2. LỊCH THANH TOÁN</h2>
                <div class="section">
                    <div class="checkbox-list">
                        <input type="checkbox" id="full-payment"> Thanh toán trọn gói: Trả một lần cho toàn bộ quá trình điều trị<br>
                        <input type="checkbox" id="by-phase"> Thanh toán theo giai đoạn: Trả khi chuyển sang giai đoạn tiếp theo
                        <p>Lưu ý: Đối với cách thanh toán trọn gói, quý khách sẽ phải thanh toán tối đa <span class="max-payment-date">2</span> giờ sau khi ký hợp đồng. Đối với trường hợp trả theo giai đoạn thì quý khách sẽ trả sau khi ký hợp đồng <span class="max-payment-date">2</span> giờ hoặc khi chuyển sang giai đoạn tiếp theo</p>
                    </div>
                </div>
            </div>

            <div class="section">
                <h2 class="section-header">3. HOÀN TIỀN VÀ CHIA RỦI RO</h2>
                <p id="refund-and-risk">Khi hoàn tiền thì những dịch vụ chưa hoàn thành hoặc là những toa thuốc chưa lấy sẽ được trả hoàn toàn. Những dịch vụ hoặc toa thuốc đã được thanh toán sẽ được hoàn trả một phần nhất định</p>
            </div>

            <div class="section">
                <h2 class="section-header">4. PHƯƠNG THỨC THANH TOÁN</h2>
                <p>Hợp đồng chấp nhận các phương thức thanh toán sau:</p>
                <ul>
                    <li>Chuyển khoản ngân hàng</li>
                    <li>Trả tiền tại quầy</li>
                </ul>
            </div>

            <div class="section">
                <h2 class="section-header">5. KÝ NHẬN</h2>
                <div class="signature-section">
                    <div class="signature-box">
                        <p>Bệnh nhân</p>
                        <signature-field
                            name="Renter's Signature"
                            role="Renter"
                            style="width: 160px; height: 80px; display: inline-block;">
                        </signature-field>
                        <p class="date" id="sign_date">Ngày ký: {sign_date}</p>
                    </div>
                </div>
            </div>
        </body>
        </html>
    """;

}
