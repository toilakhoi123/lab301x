$(document).ready(function() {
    // Custom sorting plug-in for DataTables that handles select dropdowns.
    // This function will extract the 'value' of the currently selected option.
    $.fn.dataTable.ext.order['dom-select'] = function (settings, col) {
        return this.api().column(col, { order: 'index' }).nodes().map(function (td) {
            // Find the select element and return its current value.
            // If no select element is found, return an empty string.
            return $('select', td).val();
        });
    };

    // New custom search function for DataTables.
    // This tells DataTables how to get a searchable string from a cell's content.
    $.fn.dataTable.ext.type.search['html'] = function (data) {
        // Create a temporary element to parse the HTML string
        const tempDiv = document.createElement('div');
        tempDiv.innerHTML = data;

        // Check if the cell contains a <select> element
        const selectElement = tempDiv.querySelector('select');
        if (selectElement) {
            // If a select element exists, return the text of the selected option
            const selectedOption = selectElement.querySelector('option[selected]');
            if (selectedOption) {
                return selectedOption.text;
            }
        }
        
        // For all other HTML content, return the text without tags
        return $(tempDiv).text();
    };


    var table = $('#dataTable').DataTable({
        dom: "<'row mb-2'<'col-sm-6 text-left'f><'col-sm-6 text-right'B>>" +
             "<'row'<'col-sm-12'tr>>" +
             "<'row'<'col-sm-5'i><'col-sm-7'p>>",
        buttons: [
            {
                filename: 'User Data',
                sheetName: 'User Data',
                extend: 'excelHtml5',
                text: '<i class="fas fa-file-excel"></i> Export to Excel',
                className: 'btn btn-success',
                exportOptions: {
                    columns: [0, 1, 2, 3, 4, 5, 6, 7],
                    format: {
                        body: function (data, _, column, _) {
                            // Logic for the 'role' column (column 6)
                            if (column === 6) {
                                // Create a temporary DOM element to parse the HTML string.
                                const parser = new DOMParser();
                                const doc = parser.parseFromString(data, 'text/html');
                                // Find the option element with the 'selected' attribute and return its value.
                                const selectedOption = doc.querySelector('select.role-dropdown option[selected]');
                                return selectedOption ? selectedOption.value : '';
                            }
                            // Logic for the 'disabled' column (column 7)
                            if (column === 7) {
                                // Create a temporary DOM element to parse the HTML string.
                                const parser = new DOMParser();
                                const doc = parser.parseFromString(data, 'text/html');
                                // Find the option element with the 'selected' attribute and return its value ('true' or 'false').
                                const selectedOption = doc.querySelector('select.account-disabled-dropdown option[selected]');
                                return selectedOption ? selectedOption.value : '';
                            }
                            // Default logic for other columns: remove <span> tags
                            return data.replaceAll("<span>", "").replaceAll("</span>", "");
                        }
                    }
                }
            }
        ],
        // The columnDefs configuration is the key to making the dropdowns sortable.
        // It tells DataTables to use the custom 'dom-select' sorting type for specific columns.
        columnDefs: [
            { targets: 6, orderDataType: 'dom-select' }, // Apply custom sorting to the Role column
            { targets: 7, orderDataType: 'dom-select' }  // Apply custom sorting to the Account Disabled column
        ],
        order: [[0, 'desc']]
    });

    // Custom filter function for the 'last login' column.
    $.fn.dataTable.ext.search.push(
        function(_, data, _) {
            var filterValue = $('#loginFilter').val();
            if (!filterValue) {
                return true;
            }

            // get 6th column
            var lastLogin = data[5];

            // never logged in -> always show
            if (lastLogin === "Never") {
                return true;
            }

            // invalid date
            var parts = lastLogin.split("/");
            if (parts.length !== 3) {
                return false;
            }

            // parse date
            var day = parseInt(parts[0], 10);
            var month = parseInt(parts[1], 10) - 1; // js date: 0-index system
            var year = parseInt(parts[2], 10);

            // calculate diff
            var loginDate = new Date(year, month, day);
            var now = new Date();
            var diffTime = Math.abs(now - loginDate);
            var diffDays = diffTime / (1000 * 60 * 60 * 24);

            return diffDays >= parseInt(filterValue);
        }
    );

    // Event listener to trigger filtering
    $('#loginFilter').on('change', function() {
        table.draw();
    });
});
