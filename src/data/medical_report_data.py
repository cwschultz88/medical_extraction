import datetime


class MedicalReportData(object):
    '''
    Stores data extracted from input medical reports
    '''
    def __init__(self, report_contents="", date_report_created=datetime.datetime.today()):
        self.report_contents = report_contents
        self.report_date = date_report_created
