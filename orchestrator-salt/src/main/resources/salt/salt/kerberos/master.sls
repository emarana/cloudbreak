{%- from 'kerberos/settings.sls' import kerberos with context %}

include:
  - {{ slspath }}.common

{% if kerberos.url is none or kerberos.url == '' %}

add_principals_sh_script:
  file.managed:
    - name: /opt/salt/principals.sh
    - source: salt://kerberos/scripts/principals.sh
    - template: jinja
    - skip_verify: True
    - makedirs: True
    - mode: 755
    - context:
      realm: {{ kerberos.realm }}
      kdcs: {{ kerberos.kdcs }}
      pw: {{ kerberos.password }}
      usr: {{ kerberos.user }}
{% if grains['os_family'] == 'Suse' %}
      kadmin_local: /usr/lib/mit/sbin/kadmin.local
{% else %}
      kadmin_local: kadmin.local
{% endif %}

run_principals_sh_script:
  cmd.run:
    - name: sh -x /opt/salt/principals.sh 2>&1 | tee -a /var/log/principals_sh.log && exit ${PIPESTATUS[0]}
    - require:
      - file: add_principals_sh_script
    - output_loglevel: quiet

remove_kprop_acl:
  file.absent:
    - name: /var/kerberos/krb5kdc/kpropd.acl

start_kadmin:
  service.running:
    - enable: True
{% if grains['os_family'] == 'Suse' %}
    - name: kadmind
{% else %}
    - name: kadmin
{% endif %}
    - watch:
        - pkg: install_kerberos

start_master_kdc:
  service.running:
    - enable: True
    - name: krb5kdc
    - watch:
      - pkg: install_kerberos

stop_kpropd:
  service.dead:
    - enable: False
    - name: kpropd

{% endif %}

create_krb5_conf_initialized:
  cmd.run:
    - name: touch /var/krb5-conf-initialized
    - shell: /bin/bash
    - unless: test -f /var/krb5-conf-initialized