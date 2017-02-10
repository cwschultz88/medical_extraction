
class IInfoExtractor(object):
    '''
    Interface that defines
    '''
    @classmethod
    def extract_info(cls, medical_reports):
        '''
        should return list of extracted info and correspoding values (dictionary formated) for each report in medical_reports
        '''
        pass