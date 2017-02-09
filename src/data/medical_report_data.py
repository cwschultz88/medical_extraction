from datetime import date


class MedicalReportData(object):
    def __init__(self, report_contents="", date_report_created=date.today()):
        self.report_contents = report_contents
        self.date_report_created = date_report_created
