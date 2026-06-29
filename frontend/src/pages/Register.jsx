import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/client';

// ---------------------------------------------------------------------------
// Validation rules
// ---------------------------------------------------------------------------

const validators = {
  name: (v) => {
    if (!v.trim()) return 'Nome é obrigatório.';
    if (v.trim().length < 3) return 'Nome deve ter ao menos 3 caracteres.';
    return '';
  },

  email: (v) => {
    if (!v.trim()) return 'Email é obrigatório.';
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v)) return 'Informe um email válido.';
    return '';
  },

  cpf: (v) => {
    if (!v) return 'CPF é obrigatório.';
    if (v.length !== 11) return 'CPF deve conter exatamente 11 dígitos.';
    return '';
  },

  phone: (v) => {
    if (!v) return ''; // opcional
    if (v.length < 10 || v.length > 11) return 'Telefone deve conter 10 ou 11 dígitos.';
    return '';
  },

  password: (v) => {
    if (!v) return 'Senha é obrigatória.';
    if (v.length < 6) return 'Senha deve ter no mínimo 6 caracteres.';
    if (!/[A-Z]/.test(v)) return 'Senha deve conter ao menos uma letra maiúscula.';
    return '';
  },

  birthDate: (v) => {
    if (!v) return 'Data de nascimento é obrigatória.';
    const parts = v.split('-');
    if (parts.length !== 3 || parts[0].length !== 4) {
      return 'Data inválida. Use o formato AAAA-MM-DD com 4 dígitos no ano.';
    }
    const year = parseInt(parts[0], 10);
    if (year < 1900 || year > new Date().getFullYear()) {
      return `Ano inválido: ${year}. Verifique a data informada.`;
    }
    return '';
  },

  gender: () => '',
};

// ---------------------------------------------------------------------------
// Component
// ---------------------------------------------------------------------------

const Register = () => {
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    cpf: '',
    phone: '',
    password: '',
    birthDate: '',
    gender: 'MALE',
  });

  // Per-field error messages
  const [fieldErrors, setFieldErrors] = useState({});

  // Touched state — only show error after user interacted with the field
  const [touched, setTouched] = useState({});

  // Global submit-level error (e.g. server errors)
  const [submitError, setSubmitError] = useState('');

  const navigate = useNavigate();

  // -------------------------------------------------------------------------
  // handleChange — applies input masking and per-field validation on the fly
  // -------------------------------------------------------------------------
  const handleChange = (e) => {
    const { name, value } = e.target;
    let sanitized = value;

    // Masking: strip non-numeric chars for CPF and phone
    if (name === 'cpf') {
      sanitized = value.replace(/\D/g, '').slice(0, 11);
    }
    if (name === 'phone') {
      sanitized = value.replace(/\D/g, '').slice(0, 11);
    }

    // Birth date guard: prevent year portion > 4 digits from being stored
    if (name === 'birthDate' && value) {
      const parts = value.split('-');
      if (parts[0] && parts[0].length > 4) {
        // Truncate year to 4 digits — discard the malformed value entirely
        return;
      }
    }

    const updatedData = { ...formData, [name]: sanitized };
    setFormData(updatedData);

    // Re-validate the field if it was already touched
    if (touched[name]) {
      setFieldErrors((prev) => ({
        ...prev,
        [name]: validators[name] ? validators[name](sanitized) : '',
      }));
    }
  };

  // -------------------------------------------------------------------------
  // handleBlur — mark field as touched and run validation
  // -------------------------------------------------------------------------
  const handleBlur = (e) => {
    const { name, value } = e.target;
    setTouched((prev) => ({ ...prev, [name]: true }));
    setFieldErrors((prev) => ({
      ...prev,
      [name]: validators[name] ? validators[name](value) : '',
    }));
  };

  // -------------------------------------------------------------------------
  // getInputClass — returns CSS class based on validation state
  // -------------------------------------------------------------------------
  const getInputClass = (fieldName) => {
    if (!touched[fieldName]) return 'form-control';
    return `form-control ${fieldErrors[fieldName] ? 'is-invalid' : 'is-valid'}`;
  };

  // -------------------------------------------------------------------------
  // handleSubmit — full validation before hitting the server
  // -------------------------------------------------------------------------
  const handleSubmit = async (e) => {
    e.preventDefault();
    setSubmitError('');

    // Mark all fields as touched so every error shows
    const allTouched = Object.fromEntries(
      Object.keys(formData).map((k) => [k, true])
    );
    setTouched(allTouched);

    // Run all validators
    const errors = {};
    Object.keys(validators).forEach((field) => {
      const msg = validators[field](formData[field] ?? '');
      if (msg) errors[field] = msg;
    });

    setFieldErrors(errors);

    // Abort if any error exists
    if (Object.keys(errors).length > 0) return;

    try {
      await api.post('/users/register', formData);
      navigate('/login');
    } catch (err) {
      if (err.response?.data?.details) {
        // Map server-side field errors back into fieldErrors state
        const serverErrors = err.response.data.details;
        setFieldErrors((prev) => ({ ...prev, ...serverErrors }));
      } else if (err.response?.data?.error) {
        setSubmitError(err.response.data.error);
      } else {
        setSubmitError('Erro ao registrar. Verifique os dados e tente novamente.');
      }
    }
  };

  // -------------------------------------------------------------------------
  // Render helper — renders a field error span
  // -------------------------------------------------------------------------
  const FieldError = ({ field }) =>
    touched[field] && fieldErrors[field] ? (
      <span className="field-error" role="alert">
        {fieldErrors[field]}
      </span>
    ) : null;

  // -------------------------------------------------------------------------
  // JSX
  // -------------------------------------------------------------------------
  return (
    <div style={{ maxWidth: '600px', margin: '0 auto', marginTop: '2rem' }}>
      <h2 style={{ marginBottom: '1.5rem', textAlign: 'center' }}>Cadastre-se</h2>

      {/* Global server-level error */}
      {submitError && (
        <div
          style={{
            color: 'var(--color-danger)',
            marginBottom: '1rem',
            padding: '0.65rem 0.75rem',
            backgroundColor: 'rgba(239, 68, 68, 0.1)',
            borderRadius: '6px',
            border: '1px solid rgba(239, 68, 68, 0.3)',
            fontSize: '0.9rem',
          }}
        >
          {submitError}
        </div>
      )}

      <form onSubmit={handleSubmit} noValidate>

        {/* Nome */}
        <div className="form-group">
          <label htmlFor="reg-name">Nome Completo</label>
          <input
            id="reg-name"
            type="text"
            name="name"
            className={getInputClass('name')}
            value={formData.name}
            onChange={handleChange}
            onBlur={handleBlur}
            autoComplete="name"
          />
          <FieldError field="name" />
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>

          {/* Email */}
          <div className="form-group">
            <label htmlFor="reg-email">Email</label>
            <input
              id="reg-email"
              type="email"
              name="email"
              className={getInputClass('email')}
              value={formData.email}
              onChange={handleChange}
              onBlur={handleBlur}
              autoComplete="email"
            />
            <FieldError field="email" />
          </div>

          {/* CPF */}
          <div className="form-group">
            <label htmlFor="reg-cpf">CPF (apenas números)</label>
            <input
              id="reg-cpf"
              type="text"
              name="cpf"
              inputMode="numeric"
              className={getInputClass('cpf')}
              value={formData.cpf}
              onChange={handleChange}
              onBlur={handleBlur}
              maxLength={11}
              placeholder="00000000000"
            />
            <FieldError field="cpf" />
          </div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>

          {/* Telefone */}
          <div className="form-group">
            <label htmlFor="reg-phone">Telefone (opcional)</label>
            <input
              id="reg-phone"
              type="text"
              name="phone"
              inputMode="numeric"
              className={getInputClass('phone')}
              value={formData.phone}
              onChange={handleChange}
              onBlur={handleBlur}
              maxLength={11}
              placeholder="00000000000"
            />
            <FieldError field="phone" />
          </div>

          {/* Data de Nascimento */}
          <div className="form-group">
            <label htmlFor="reg-birthDate">Data de Nascimento</label>
            <input
              id="reg-birthDate"
              type="date"
              name="birthDate"
              className={getInputClass('birthDate')}
              value={formData.birthDate}
              onChange={handleChange}
              onBlur={handleBlur}
              min="1900-01-01"
              max={new Date().toISOString().split('T')[0]}
            />
            <FieldError field="birthDate" />
          </div>
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>

          {/* Gênero */}
          <div className="form-group">
            <label htmlFor="reg-gender">Gênero</label>
            <select
              id="reg-gender"
              name="gender"
              className="form-control"
              value={formData.gender}
              onChange={handleChange}
            >
              <option value="MALE">Masculino</option>
              <option value="FEMALE">Feminino</option>
              <option value="OTHER">Outro</option>
            </select>
          </div>

          {/* Senha */}
          <div className="form-group">
            <label htmlFor="reg-password">Senha</label>
            <input
              id="reg-password"
              type="password"
              name="password"
              className={getInputClass('password')}
              value={formData.password}
              onChange={handleChange}
              onBlur={handleBlur}
              autoComplete="new-password"
            />
            <FieldError field="password" />
          </div>
        </div>

        <button
          type="submit"
          className="btn btn-primary"
          style={{ width: '100%', marginTop: '1rem' }}
        >
          Concluir Cadastro
        </button>
      </form>
    </div>
  );
};

export default Register;
