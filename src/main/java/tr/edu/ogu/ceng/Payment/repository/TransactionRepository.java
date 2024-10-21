package tr.edu.ogu.ceng.Payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tr.edu.ogu.ceng.Payment.model.Transaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
