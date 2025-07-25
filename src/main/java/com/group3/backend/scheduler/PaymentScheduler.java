package com.group3.backend.scheduler;

import com.group3.backend.model.Payment;
import com.group3.backend.repository.PaymentRepository;
import com.group3.backend.service.PaymentService;
import com.group3.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class PaymentScheduler {
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private EmailService emailService;

    // Xử lý hóa đơn quá hạn
    @Scheduled(cron = "0 5 * * * ?") // Chạy mỗi giờ vào phút thứ 5
    public void checkUnpaidPayments() {
        
        // Find all pending payments that have passed their deadline
        List<Payment> unpaidPayments = paymentRepository.findByStatusAndPaymentDeadlineLessThan(
            Payment.Status.PENDING,
            LocalDateTime.now()
        );

        // For each unpaid payment, cancel the schedules
        for (Payment payment : unpaidPayments) {
            paymentService.cancelPayment(payment.getId());

            // Sau khi huỷ, gửi email thông báo
            String email = payment.getUser().getEmail();
            String name = payment.getUser().getFullName();

            String content = """
                <p>Xin chào %s,</p>
                <p>Đây là thông báo rằng hóa đơn của bạn đã quá hạn thanh toán và đã bị huỷ.</p>
                <p>Các lịch khám hoặc dịch vụ liên quan cũng đã bị hủy.</p>
                """.formatted(name);

            emailService.sendReminderEmail(email, "Hóa đơn đã bị huỷ do quá hạn thanh toán", content);
        }
    }

    // Nhắc nhở thanh toán hóa đơn sắp đến hạn
    @Scheduled(cron = "0 0 6 * * *") // Chạy mỗi ngày lúc 6 giờ sáng
    public void remindUpcomingPayments() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime from = now.plusDays(1).withHour(0).withMinute(0);
        LocalDateTime to = from.plusDays(1);

        List<Payment> upcomingPayments = paymentRepository.findByStatusAndPaymentDeadlineBetween(
                Payment.Status.PENDING, from, to);

        for (Payment payment : upcomingPayments) {
            String email = payment.getUser().getEmail();
            String name = payment.getUser().getFullName();
            String deadline = payment.getPaymentDeadline().format(DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy"));

            String content = """
                <p>Xin chào %s,</p>
                <p>Đây là lời nhắc bạn có hóa đơn cần thanh toán trước <strong>%s</strong>.</p>
                <p>Vui lòng đăng nhập để thanh toán đúng hạn để đảm bảo lịch khám không bị hủy.</p>
                """.formatted(name, deadline);

            emailService.sendReminderEmail(email, "Nhắc nhở có hóa đơn cần thanh toán", content);
        }
    }
}
