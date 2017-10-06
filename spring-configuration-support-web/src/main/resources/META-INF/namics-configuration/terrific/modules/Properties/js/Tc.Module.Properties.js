(function ($) {
	Tc.Module.Properties = Tc.Module.extend({

		url: '',
		tpl: null,
		$values: null,
		$dialog: null,
		$key: null,
		$value: null,
		$form: null,
		$error: null,

		on: function (callback) {

			var self = this;
			if (self.$ctx.data('url')) {
				self.url = self.$ctx.data('url');
				self.tpl = doT.template(self.$ctx.find('.tpl-props').text());
				self._loadJobs(self, callback);
			}
			else {
				callback();
			}

			self.$values = $('.props', self.$ctx);
			self.$dialog = $('.bs-modal-edit', self.$ctx);
			self.$key = $('[name="key"]', self.$ctx);
			self.$value = $('[name="value"]', self.$ctx);
			self.$form = $('form', self.$dialog);
			self.$error = $('#save-error', self.$ctx);
		},

		after: function () {
			var self = this;

			self.$form.on('submit', function (e) {
				e.preventDefault();

				var data = self.$form.serializeArray();
				$.ajax({
					"url": self.url,
					"type": "POST",
					"data": data,
					success: function () {
						self._loadJobs(self, function () {
							self.$dialog.modal('hide');
							self._higlightSuccess(self.$key.val(), self);
						});
					},
					error: function () {
						self.$error.show();
					}
				});

			});
		},


		_loadJobs: function (self, callback) {
			$.getJSON(self.url, function (data) {
				if (data.insertSupported === true) {
					$('.js-insert', self.$ctx).on('click', function (e) {
						e.preventDefault();

						var $this = $(this);

						self.$key.val('').prop("readonly", false);
						self.$value.val('');
						self.$error.hide();
						self.$dialog.modal('show');
					});
				}
				else {
					$('.js-insert', self.$ctx).hide();
				}
				$('.props', self.$ctx).html(self.tpl(data.properties));

				$('[data-property]', self.$ctx).on('click', function (e) {
					e.preventDefault();

					var $this = $(this);
					var key = $this.data('property');
					var value = $this.data('value');
					var secret = $this.data('secret');

					self.$key.val(key).prop("readonly", true);
					self.$value.val(secret ? '' : value);
					self.$error.hide();
					self.$dialog.modal('show');
				});
				if (data.deleteSupported === true) {
					$('[data-delete]', self.$ctx).on('click', function (e) {
						e.preventDefault();
						var $this = $(this);
						var key = $this.attr("data-delete");
						var env = $this.attr("data-env");
						$.ajax({
							"url": self.url + "/delete",
							"type": "POST",
							"data": {"key": key, "env": env},
							"success": function () {
								self._loadJobs(self);
							}
						});

					});
					$('[data-delete]', self.$ctx).show();
				}
				else {
					$('[data-delete]', self.$ctx).hide();
				}

				if (callback) {
					callback();
				}
			});
		},

		_higlightSuccess: function (key, self) {
			var $row = $('[data-property="' + key + '"]', self.$ctx).parents('tr');
			$row.addClass('success');
			setTimeout(function () {
				$row.removeClass('success');
			}, 1500);
		}

	});
})(Tc.$);