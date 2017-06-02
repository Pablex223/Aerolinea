function validar() {
    $('.decimalPlaces').keyup(function (event) {
        if (event.which >= 37 && event.which <= 40)
            return;

        $(this).val(function (index, value) {
            return value.replace(/\D/g, '').replace(/\B(?=(\d{2})+(?!\d))/g, '.');
        });
    });

    $('.decimal').keyup(function () {
        if (this.value.match(/[^0-9.]/g)) {
            this.value = this.value.replace(/[^0-9.]/g, '');
        }
    });

    $('.id').keyup(function () {
        if (this.value.match(/[^0-9-]/g)) {
            this.value = this.value.replace(/[^0-9-]/g, '');
        }
    });

    $('.numeric').keyup(function () {
        if (this.value.match(/[^0-9]/g)) {
            this.value = this.value.replace(/[^0-9]/g, '');
        }
    });

    $('.email').keyup(function () {
        if (this.value.match(/[^a-zA-Z0-9@._-]/g)) {
            this.value = this.value.replace(/[^a-zA-Z0-9@._-]/g, '');
        }
    });

    $('.alphanumeric').keyup(function () {
        if (this.value.match(/[^a-zA-Z0-9]/g)) {
            this.value = this.value.replace(/[^a-zA-Z0-9]/g, '');
        }
    });

    $('.char').keyup(function () {
        if (this.value.match(/[^a-zA-Z]/g)) {
            this.value = this.value.replace(/[^a-zA-Z]/g, '');
        }
    });

    $('.charWithSpace').keyup(function () {
        if (this.value.match(/[^a-zA-Z ]/g)) {
            this.value = this.value.replace(/[^a-zA-Z ]/g, '');
        }
    });

    $('.alphanumericWithSpace').keyup(function () {
        if (this.value.match(/[^a-zA-Z0-9 ]/g)) {
            this.value = this.value.replace(/[^a-zA-Z0-9 ]/g, '');
        }
    });

    $('.charToUpperCase').keyup(function () {
        if (this.value.match(/[^a-zA-Z]/g)) {
            this.value = this.value.replace(/[^a-zA-Z]/g, '');
        }
        this.value = this.value.toString().toUpperCase();
    });

    $('.charWithSpaceToUpperCase').keyup(function () {
        if (this.value.match(/[^a-zA-Z ]/g)) {
            this.value = this.value.replace(/[^a-zA-Z ]/g, '');
        }

        this.value = this.value.toString().toUpperCase();
    });

    $('.alphanumericWithSpacetoUpperCase').keyup(function () {
        if (this.value.match(/[^a-zA-Z0-9 ]/g)) {
            this.value = this.value.replace(/[^a-zA-Z0-9 ]/g, '');
        }

        this.value = this.value.toString().toUpperCase();
    });

    $('.alphanumericToUpperCase').keyup(function () {
        if (this.value.match(/[^a-zA-Z0-9]/g)) {
            this.value = this.value.replace(/[^a-zA-Z0-9]/g, '');
        }

        this.value = this.value.toString().toUpperCase();
    });

}


