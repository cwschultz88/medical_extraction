class Medicine(object):
    def __init__(self, identification_name):
        self.identification_name = indentification_name
        self.brand_names = set()
        self.dosages = []

    def __hash__(self):
        hash(self.identification_name)

    def add_dosage(self, dosage_type_str, dosage_amount_str):
        self.dosages.append((dosage_type_str, dosage_amount_str))

    def add_brand_names(self, brand_name):
        self.brand_names.add(brand_names)
