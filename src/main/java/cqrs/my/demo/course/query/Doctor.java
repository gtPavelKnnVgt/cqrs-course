package cqrs.my.demo.course.query;

record Doctor(Long id, String firstName, String lastName, String specialization,
              String licenseNumber, Department department) {
}
